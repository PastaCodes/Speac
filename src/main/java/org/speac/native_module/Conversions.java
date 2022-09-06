package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.language.Data;
import org.speac.data_types.SpeacInteger;
import org.speac.data_types.SpeacReal;
import org.speac.data_types.SpeacString;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.StringUtils;

public final class Conversions {
	public static final CoreFunctionCallable AS_STRING
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		Data<?> input = arguments.get(0);
		returnValue.set(new SpeacString(input.toString(false)));
		return null; // No errors
	};

	public static final CoreFunctionCallable AS_NUMBER
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "as number");
		String base = ((SpeacString) arguments.get(0)).value();
		if (StringUtils.stringMatchesInteger(base))
			returnValue.set(new SpeacInteger(Integer.parseInt(base)));
		else if (StringUtils.stringMatchesDouble(base))
			returnValue.set(new SpeacReal(Double.parseDouble(base)));
		else
			return new SpeacPartialError(CoreModuleErrors.ILLEGAL_CONVERSION, base);
		return null; // No errors
	};
}
