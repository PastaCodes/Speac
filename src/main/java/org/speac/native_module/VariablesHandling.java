package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.SpeacParser;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.internal.TokenizedName;
import org.speac.core.types.language.Data;
import org.speac.core.types.language.Variable;
import org.speac.data_types.SpeacString;
import org.speac.errors.CoreModuleErrors;

public final class VariablesHandling {
	public static final CoreFunctionCallable CREATE_AS
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING,
				Data.ANY_TYPE
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "create as");
		String baseName = ((SpeacString) arguments.get(0)).value();
		Data<?> newValue = arguments.get(1);
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		scope.addVariable(new Variable(parsedName, newValue));
		return null; // No errors
	};

	public static final CoreFunctionCallable SET_TO
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING,
				Data.ANY_TYPE
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "set to");
		String baseName = ((SpeacString) arguments.get(0)).value();
		Data<?> newValue = arguments.get(1);
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		Variable variable = scope.fetchVariable(parsedName);
		if (variable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, baseName);
		variable.contents = newValue;
		return null; // No errors
	};

	public static final CoreFunctionCallable VALUE_OF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "value of");
		String baseName = ((SpeacString) arguments.get(0)).value();
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		Variable variable = scope.fetchVariable(parsedName);
		if (variable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, baseName);
		returnValue.set(variable.contents);
		return null; // No errors
	};

	public static final CoreFunctionCallable TYPE_OF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		returnValue.set(new SpeacString(arguments.get(0).type().toString()));
		return null; // No errors
	};

	public static final CoreFunctionCallable LEND
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "lend");
		String baseName = ((SpeacString) arguments.get(0)).value();
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		if (parentScope == null)
			return new SpeacPartialError(CoreModuleErrors.ACCESSING_PARENT_OF_ROOT_SCOPE, "lend");
		Variable variable = scope.fetchVariable(parsedName);
		if (variable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, baseName);
		parentScope.addVariable(variable);
		return null; // No errors
	};

	public static final CoreFunctionCallable BORROW
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "borrow");
		String baseName = ((SpeacString) arguments.get(0)).value();
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		if (parentScope == null)
			return new SpeacPartialError(CoreModuleErrors.ACCESSING_PARENT_OF_ROOT_SCOPE, "borrow");
		Variable variable = parentScope.fetchVariable(parsedName);
		if (variable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, baseName);
		scope.addVariable(variable);
		return null; // No errors
	};

	public static final CoreFunctionCallable ADD_NAME_TO
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "add name to");

		String alias = ((SpeacString) arguments.get(0)).value();
		String old = ((SpeacString) arguments.get(1)).value();

		TokenizedName parsedAlias = SpeacParser.parseVariableName(alias);
		TokenizedName parsedOld = SpeacParser.parseVariableName(old);

		Variable mentionedVariable = scope.fetchVariable(parsedOld);
		if (mentionedVariable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, old);

		mentionedVariable.addName(parsedAlias);
		return null; // No errors
	};
}
