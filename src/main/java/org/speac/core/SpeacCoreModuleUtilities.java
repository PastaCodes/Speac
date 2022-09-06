package org.speac.core;

import org.speac.core.types.error.SpeacError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.internal.Scope;
import org.speac.core.types.internal.VariadicPrototype;
import org.speac.core.types.language.CoreFunction;
import org.speac.core.types.language.Data;
import org.speac.core.types.language.Variable;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.FixedList;

public final class SpeacCoreModuleUtilities {
	public static void defineCoreFunction(Scope scope, String prototypeFormat, CoreFunctionCallable callable) {
		scope.addFunction(new CoreFunction(SpeacParser.parseEmptyPrototype(prototypeFormat), callable));
	}

	public static void defineVariadicCoreFunction(Scope scope, String name, CoreFunctionCallable callable) {
		scope.addFunction(new CoreFunction(new VariadicPrototype(SpeacParser.parseVariableName(name)), callable));
	}

	public static void defineVariable(Scope scope, String nameBase, Data<?> value) {
		scope.addVariable(new Variable(SpeacParser.parseVariableName(nameBase), value));
	}

	public static SpeacError validateArguments(FixedList<Data<?>> gotArguments, Data.Type ... expectedTypes) {
		for (int index = 0; index < gotArguments.size(); index++)
			if (expectedTypes[index] != Data.ANY_TYPE
					&& gotArguments.get(index).type() != expectedTypes[index])
				return new CoreModuleErrors.InvalidArgumentType(index, expectedTypes[index], gotArguments.get(index).type());

		return null; // No errors
	}
}
