package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.SpeacParser;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.internal.TokenizedName;
import org.speac.core.types.language.Data;
import org.speac.core.types.language.Variable;
import org.speac.data_types.SpeacInteger;
import org.speac.data_types.SpeacList;
import org.speac.data_types.SpeacString;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.FixedList;

import java.util.ArrayList;
import java.util.LinkedList;

public final class ListsHandling {
	public static final CoreFunctionCallable LIST_WITH
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.size() < 1)
			return new SpeacPartialError(new CoreModuleErrors.NotEnoughVarargs(1, arguments.size()), "list with");
		returnValue.set(new SpeacList(arguments));
		return null; // No errors
	};

	public static final CoreFunctionCallable LENGTH_OF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.LIST
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "length of");
		returnValue.set(new SpeacInteger(((SpeacList) arguments.get(0)).value().size()));
		return null; // No errors
	};

	public static final CoreFunctionCallable ITEM_OF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.INTEGER,
				Data.Type.LIST
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "item of");
		int index = ((SpeacInteger) arguments.get(0)).value();
		FixedList<Data<?>> items = ((SpeacList) arguments.get(1)).value();
		if (index < 0 || index >= items.size())
			return new SpeacPartialError(CoreModuleErrors.INDEX_ERROR, Integer.toString(index));
		returnValue.set(items.get(index));
		return null; // No errors
	};

	public static CoreFunctionCallable SET_ITEM_OF_TO
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.INTEGER,
				Data.Type.STRING,
				Data.ANY_TYPE
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "set item of to");
		int index = ((SpeacInteger) arguments.get(0)).value();
		String baseName = ((SpeacString) arguments.get(1)).value();
		Data<?> newValue = arguments.get(2);
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		Variable variable = scope.fetchVariable(parsedName);
		if (variable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, baseName);
		if (!(variable.contents instanceof SpeacList))
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidReferencedType(Data.Type.LIST, variable.contents.type()),
					baseName);
		ArrayList<Data<?>> items = new ArrayList<>(((SpeacList) variable.contents).value());
		if (index < 0 || index >= items.size())
			return new SpeacPartialError(CoreModuleErrors.INDEX_ERROR, Integer.toString(index));
		items.set(index, newValue);
		variable.contents = new SpeacList(new FixedList<>(items));
		return null; // No errors
	};

	public static final CoreFunctionCallable APPEND_TO
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.ANY_TYPE,
				Data.Type.STRING
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "append to");
		Data<?> newValue = arguments.get(0);
		String baseName = ((SpeacString) arguments.get(1)).value();
		TokenizedName parsedName = SpeacParser.parseVariableName(baseName);
		Variable variable = scope.fetchVariable(parsedName);
		if (variable == null)
			return new SpeacPartialError(CoreModuleErrors.UNKNOWN_VARIABLE_NAME, baseName);
		if (!(variable.contents instanceof SpeacList))
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidReferencedType(Data.Type.LIST, variable.contents.type()),
					baseName);
		FixedList<Data<?>> oldItems = ((SpeacList) variable.contents).value();
		LinkedList<Data<?>> newItems = new LinkedList<>(oldItems);
		newItems.add(newValue);
		variable.contents = new SpeacList(new FixedList<>(newItems));
		return null; // No errors
	};
}
