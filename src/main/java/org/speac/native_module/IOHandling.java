package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.internal.TokenizedName;
import org.speac.core.types.language.Data;
import org.speac.data_types.SpeacString;
import org.speac.errors.CoreModuleErrors;

public final class IOHandling {
	public static final CoreFunctionCallable SAY
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.size() < 1)
			return new SpeacPartialError(new CoreModuleErrors.NotEnoughVarargs(1, arguments.size()), "say");
		StringBuilder line = new StringBuilder();
		for (Data<?> argument : arguments)
			line.append(argument.toString(true));
		runner.wrappersConsoleOut(line.toString());
		return null; // No errors
	};

	public static final CoreFunctionCallable ASK
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "ask");
		runner.mainWrapper.consoleOut(((SpeacString) arguments.get(0)).value());
		scope.fetchVariable(new TokenizedName("Answer")).contents = new SpeacString(runner.mainWrapper.consoleIn());
		return null; // No errors
	};
}
