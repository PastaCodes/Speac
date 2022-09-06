package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.types.error.SpeacCompleteError;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.language.CodeBlock;
import org.speac.core.types.language.Data;
import org.speac.core.types.language.SystemVariable;
import org.speac.data_types.SpeacBoolean;
import org.speac.data_types.SpeacCodeBlock;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.Reference;

public final class Conditionals {
	public static final SystemVariable.Identifier<SpeacBoolean> IF_RESULT_IDENTIFIER = new SystemVariable.Identifier<>();

	public static final CoreFunctionCallable IF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.BOOLEAN,
				Data.Type.CODE_BLOCK
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "if");
		Boolean result = ((SpeacBoolean) arguments.get(0)).value();
		CodeBlock body = ((SpeacCodeBlock) arguments.get(1)).value();
		scope.addSystemVariable(new SystemVariable<>(Conditionals.IF_RESULT_IDENTIFIER, new SpeacBoolean(result)));
		if (result) {
			Reference<SpeacCompleteError> blockRaised = new Reference<>();
			runner.runCodeBlock(body.lines(), body.scope(), scope, breakLevel, blockRaised);
			if (blockRaised.isSet())
				return blockRaised.get();
		}
		return null; // No errors
	};

	public static final CoreFunctionCallable ELSE_IF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.BOOLEAN,
				Data.Type.CODE_BLOCK
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "else if");
		SpeacBoolean previousResult = scope.accessSystemVariable(Conditionals.IF_RESULT_IDENTIFIER);
		if (previousResult == null)
			return new SpeacPartialError(CoreModuleErrors.MISSING_IF, "else if");
		if (!previousResult.value()) {
			Boolean newResult = ((SpeacBoolean) arguments.get(0)).value();
			CodeBlock body = ((SpeacCodeBlock) arguments.get(1)).value();
			scope.addSystemVariable(new SystemVariable<>(Conditionals.IF_RESULT_IDENTIFIER,
					new SpeacBoolean(newResult)));
			if (newResult) {
				Reference<SpeacCompleteError> blockRaised = new Reference<>();
				runner.runCodeBlock(body.lines(), body.scope(), scope, breakLevel, blockRaised);
				if (blockRaised.isSet())
					return blockRaised.get();
			}
		}
		return null; // No errors
	};

	public static final CoreFunctionCallable ELSE
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.CODE_BLOCK
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "else");
		SpeacBoolean previousResult = scope.accessSystemVariable(Conditionals.IF_RESULT_IDENTIFIER);
		if (previousResult == null)
			return new SpeacPartialError(CoreModuleErrors.MISSING_IF, "else");
		if (!previousResult.value()) {
			CodeBlock body = ((SpeacCodeBlock) arguments.get(0)).value();
			Reference<SpeacCompleteError> blockRaised = new Reference<>();
			runner.runCodeBlock(body.lines(), body.scope(), scope, breakLevel, blockRaised);
			if (blockRaised.isSet())
				return blockRaised.get();
		}
		return null; // No errors
	};
}
