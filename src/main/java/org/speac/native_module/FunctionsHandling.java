package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.SpeacParser;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.*;
import org.speac.core.types.language.*;
import org.speac.data_types.SpeacCodeBlock;
import org.speac.data_types.SpeacString;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.FixedList;
import org.speac.utilities.Reference;

import java.util.LinkedList;

public final class FunctionsHandling {
	public static final SystemVariable.Identifier<Data<?>> RETURN_VALUE_IDENTIFIER = new SystemVariable.Identifier<>();

	public static final CoreFunctionCallable DEFINE
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING,
				Data.Type.CODE_BLOCK
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "define");

		String prototypeBase = ((SpeacString) arguments.get(0)).value();
		CodeBlock codeArgument = ((SpeacCodeBlock) arguments.get(1)).value();

		Reference<FixedList<Argument>> parameters = new Reference<>();
		Prototype prototype = SpeacParser.parsePrototype(prototypeBase, parameters);
		if (prototype == null)
			return new SpeacPartialError(CoreModuleErrors.INVALID_PROTOTYPE, prototypeBase);

		SpeacFunction defined;

		if (prototype instanceof VariadicPrototype) {
			defined = new SpeacFunction(prototype, new FixedList<>(new TokenizedName("...")), codeArgument);
		} else {
			LinkedList<TokenizedName> tokenizedParameters = new LinkedList<>();
			for (int parametersIndex = 0; parametersIndex < parameters.get().size(); parametersIndex++) {
				Argument argument = parameters.get().get(parametersIndex);
				if (argument.type() != Argument.Type.VARIABLE)
					return new SpeacPartialError(CoreModuleErrors.INVALID_PROTOTYPE, prototypeBase);

				TokenizedName parsed = SpeacParser.parseVariableName(argument.contents());

				// Check if parameter is duplicate
				for (TokenizedName parameter : tokenizedParameters) {
					if (parameter.matches(parsed))
						return new SpeacPartialError(CoreModuleErrors.DUPLICATE_PARAMETER, argument.contents());
				}

				tokenizedParameters.add(parsed);
			}
			defined = new SpeacFunction(prototype, new FixedList<>(tokenizedParameters), codeArgument);
		}

		defined.body.scope().addFunction(defined); // Enable recursion
		scope.addFunction(defined);
		return null; // No errors
	};

	public static final CoreFunctionCallable RETURN
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		scope.addSystemVariable(new SystemVariable<>(FunctionsHandling.RETURN_VALUE_IDENTIFIER, arguments.get(0)));
		breakLevel.setReturned();
		return null; // No errors
	};

	public static final CoreFunctionCallable ADD_ALIAS_TO
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "add alias to");

		String alias = ((SpeacString) arguments.get(0)).value();
		String old = ((SpeacString) arguments.get(1)).value();

		VariadicPrototype aliasVariadicPrototype = SpeacParser.parseVariadicPrototype(alias);
		VariadicPrototype oldVariadicPrototype = SpeacParser.parseVariadicPrototype(old);

		if (aliasVariadicPrototype == null && oldVariadicPrototype == null) {
			Reference<Prototype> aliasPrototype = new Reference<>();
			Reference<FixedList<Argument>> aliasArguments = new Reference<>();
			SpeacError aliasParseError = SpeacParser.parseInstruction(alias, aliasPrototype, aliasArguments);
			if (aliasParseError != null)
				return new SpeacPartialError(CoreModuleErrors.INVALID_PROTOTYPE, alias);

			Reference<Prototype> oldPrototype = new Reference<>();
			Reference<FixedList<Argument>> oldArguments = new Reference<>();
			SpeacError oldParseError = SpeacParser.parseInstruction(old, oldPrototype, oldArguments);
			if (oldParseError != null)
				return new SpeacPartialError(CoreModuleErrors.INVALID_PROTOTYPE, old);

			// TODO decide whether to reintroduce this check
            /*
            for (Argument argument : aliasArguments.get())
                if (argument.type != Argument.Type.VARIABLE);
            */

			Reference<FixedList<Integer>> oldArgumentsCorrespondence = new Reference<>();
			Function mentionedFunction = scope.fetchFunction(oldPrototype.get(), oldArgumentsCorrespondence);
			if (mentionedFunction == null)
				return new SpeacPartialError(CoreModuleErrors.UNKNOWN_FUNCTION_NAME, old);

			if (aliasArguments.get().size() != oldArguments.get().size())
				return new SpeacPartialError(CoreModuleErrors.ALIAS_ARGUMENTS_MISMATCH, SpeacError.NO_TOKEN);

			Integer[] relativeArgumentsCorrespondence = new Integer[oldArguments.get().size()];
			for (int oldIndex = 0; oldIndex < oldArguments.get().size(); oldIndex++) {
				int aliasArgumentIndex = -1;
				for (int aliasIndex = 0; aliasIndex < aliasArguments.get().size(); aliasIndex++)
					if (aliasArguments.get().get(aliasIndex).contents().equals(oldArguments.get().get(oldIndex).contents())) {
						aliasArgumentIndex = aliasIndex;
						break;
					}
				if (aliasArgumentIndex == -1)
					return new SpeacPartialError(CoreModuleErrors.ALIAS_ARGUMENTS_MISMATCH,
							oldArguments.get().get(oldIndex).contents());

				// Check if argument is duplicate
				for (int index = 0; index < oldIndex; index++)
					if (relativeArgumentsCorrespondence[index] == aliasArgumentIndex)
						return new SpeacPartialError(CoreModuleErrors.DUPLICATE_PARAMETER,
								oldArguments.get().get(oldIndex).contents());

				relativeArgumentsCorrespondence[oldIndex] = aliasArgumentIndex;
			}

			Integer[] newArgumentsCorrespondence = new Integer[oldArguments.get().size()];
			for (int index = 0; index < newArgumentsCorrespondence.length; index++)
				newArgumentsCorrespondence[index] =
						oldArgumentsCorrespondence.get().get(relativeArgumentsCorrespondence[index]);

			mentionedFunction.addAlias(new FunctionAlias(aliasPrototype.get(), new FixedList<>(newArgumentsCorrespondence)));

		} else if (aliasVariadicPrototype != null && oldVariadicPrototype != null) {
			Function mentionedFunction = scope.fetchFunction(oldVariadicPrototype, new Reference<>());
			// Don't care about argumentsCorrespondence because it should always be VariadicAlias.VARIADIC_CORRESPONDENCE (null)
			if (mentionedFunction == null)
				return new SpeacPartialError(CoreModuleErrors.UNKNOWN_FUNCTION_NAME, old);

			mentionedFunction.addAlias(new VariadicAlias(aliasVariadicPrototype));

		} else {
			return new SpeacPartialError(new CoreModuleErrors.InconsistentVariadicAlias(
					oldVariadicPrototype == null
							? CoreModuleErrors.InconsistentVariadicAlias.Type.ALIAS_BUT_NOT_OLD
							: CoreModuleErrors.InconsistentVariadicAlias.Type.OLD_BUT_NOT_ALIAS
			), SpeacError.NO_TOKEN);
		}
		return null; // No errors
	};
}
