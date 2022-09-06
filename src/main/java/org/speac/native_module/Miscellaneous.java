package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.types.error.SpeacCompleteError;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.error.SpeacStaticError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.internal.Scope;
import org.speac.core.types.language.CodeBlock;
import org.speac.core.types.language.Data;
import org.speac.data_types.SpeacCodeBlock;
import org.speac.data_types.SpeacInteger;
import org.speac.data_types.SpeacString;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.Reference;

public final class Miscellaneous {
	public static final CoreFunctionCallable REST
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		return null; // No errors
	};

	public static final CoreFunctionCallable LEARN
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "learn");
		String moduleName = ((SpeacString) arguments.get(0)).value();
		Reference<SpeacCompleteError> moduleError = new Reference<>();
		if (!runner.importCoreModule(moduleName, scope, moduleError))
			if (!runner.importModule(moduleName, scope, moduleError))
				return new SpeacPartialError(CoreModuleErrors.INVALID_MODULE, moduleName);
		return moduleError.isSet() ? moduleError.get() : null;
	};

	public static final CoreFunctionCallable RUN
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		CodeBlock block = ((SpeacCodeBlock) arguments.get(0)).value();
		Reference<SpeacCompleteError> blockRaised = new Reference<>();
		runner.runCodeBlock(block.lines(), block.scope(), scope, breakLevel, blockRaised);
		return blockRaised.isSet() ? blockRaised.get() : null;
	};

	public static final CoreFunctionCallable RUN_HERE
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		CodeBlock block = ((SpeacCodeBlock) arguments.get(0)).value();
		Scope compoundScope = block.scope().hereditate();
		Scope.merge(compoundScope, scope);
		Reference<SpeacCompleteError> blockRaised = new Reference<>();
		runner.runCodeBlock(block.lines(), compoundScope, scope, breakLevel, blockRaised);
		return blockRaised.isSet() ? blockRaised.get() : null;
	};

	public static final CoreFunctionCallable REPEAT
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.CODE_BLOCK
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "repeat");
		CodeBlock block = ((SpeacCodeBlock) arguments.get(0)).value();
		while (true) {
			Reference<SpeacCompleteError> blockRaised = new Reference<>();
			boolean broken = runner.runCodeBlock(block.lines(), block.scope(), scope, breakLevel, blockRaised);
			if (blockRaised.isSet())
				return blockRaised.get();
			if (broken)
				break;
		}
		return null; // No errors
	};

	public static final CoreFunctionCallable BREAK_TIMES
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.INTEGER
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "break times");
		breakLevel.raise(((SpeacInteger) arguments.get(0)).value());
		return null; // No errors
	};

	public static final CoreFunctionCallable RAISE_ERROR_ON
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.STRING,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "raise error on");
		String name = ((SpeacString) arguments.get(0)).value();
		String token = ((SpeacString) arguments.get(1)).value();
		return new SpeacPartialError(
				new SpeacStaticError(name, SpeacError.NO_DESCRIPTION, SpeacError.NO_SOLUTION),
				token);
	};
}
