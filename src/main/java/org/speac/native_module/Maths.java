package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.internal.CoreFunctionCallable;
import org.speac.core.types.language.Data;
import org.speac.data_types.SpeacBoolean;
import org.speac.data_types.SpeacInteger;
import org.speac.data_types.SpeacReal;
import org.speac.errors.CoreModuleErrors;
import org.speac.utilities.FixedList;
import org.speac.utilities.MathUtils;

public final class Maths {
	private static final FixedList<Data.Type> NUMBER_TYPES = new FixedList<>(Data.Type.INTEGER, Data.Type.REAL);

	public static final CoreFunctionCallable EQUALS
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		returnValue.set(new SpeacBoolean(arguments.get(0).matches(arguments.get(1))));
		return null; // No errors
	};

	public static final CoreFunctionCallable PLUS
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			int first = ((SpeacInteger) arguments.get(0)).value();
			int second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacInteger(first + second));

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			int first = ((SpeacInteger) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first + second));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			double first = ((SpeacReal) arguments.get(0)).value();
			int second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first + second));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			double first = ((SpeacReal) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first + second));

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"plus");
		return null; // No errors
	};

	public static final CoreFunctionCallable MINUS
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			int first = ((SpeacInteger) arguments.get(0)).value();
			int second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacInteger(first - second));

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			int first = ((SpeacInteger) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first - second));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			double first = ((SpeacReal) arguments.get(0)).value();
			int second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first - second));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			double first = ((SpeacReal) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first - second));

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"minus");

		return null; // No errors
	};

	public static final CoreFunctionCallable TIMES
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			int first = ((SpeacInteger) arguments.get(0)).value();
			int second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacInteger(first * second));

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			int first = ((SpeacInteger) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first * second));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			double first = ((SpeacReal) arguments.get(0)).value();
			int second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first * second));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			double first = ((SpeacReal) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(first * second));

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"times");

		return null; // No errors
	};

	public static final CoreFunctionCallable OVER
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		double first, second;

		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			first = ((SpeacInteger) arguments.get(0)).value();
			second = ((SpeacInteger) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			first = ((SpeacInteger) arguments.get(0)).value();
			second = ((SpeacReal) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			first = ((SpeacReal) arguments.get(0)).value();
			second = ((SpeacInteger) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			first = ((SpeacReal) arguments.get(0)).value();
			second = ((SpeacReal) arguments.get(1)).value();

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"over");

		// Embrace NaN, Infinity, -Infinity...
		returnValue.set(new SpeacReal(first / second));
		return null; // No errors
	};

	public static final CoreFunctionCallable QUOTIENT_OF_OVER
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.INTEGER,
				Data.Type.INTEGER
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "add alias to");

		int first = ((SpeacInteger) arguments.get(0)).value();
		int second = ((SpeacInteger) arguments.get(1)).value();
		returnValue.set(new SpeacInteger(first / second));
		return null; // No errors
	};

	public static final CoreFunctionCallable REMAINDER_OF_OVER
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.INTEGER,
				Data.Type.INTEGER
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "add alias to");

		int first = ((SpeacInteger) arguments.get(0)).value();
		int second = ((SpeacInteger) arguments.get(1)).value();
		returnValue.set(new SpeacInteger(first % second));
		return null; // No errors
	};

	public static final CoreFunctionCallable TO_THE
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			double first = ((SpeacInteger) arguments.get(0)).value();
			double second = ((SpeacInteger) arguments.get(1)).value();
			double result = Math.pow(first, second);
			returnValue.set(second < 0 && Math.abs(first) != 1
					? new SpeacReal(result)
					: new SpeacInteger((int) result));

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			double first = ((SpeacInteger) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(Math.pow(first, second)));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			double first = ((SpeacReal) arguments.get(0)).value();
			double second = ((SpeacInteger) arguments.get(1)).value();
			returnValue.set(new SpeacReal(Math.pow(first, second)));

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			double first = ((SpeacReal) arguments.get(0)).value();
			double second = ((SpeacReal) arguments.get(1)).value();
			returnValue.set(new SpeacReal(Math.pow(first, second)));

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"to the");

		return null; // No errors
	};

	public static final CoreFunctionCallable ABSOLUTE_VALUE_OF
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		if (arguments.get(0).type() == Data.Type.INTEGER)
			returnValue.set(new SpeacInteger(Math.abs(((SpeacInteger) arguments.get(0)).value())));
		else if (arguments.get(0).type() == Data.Type.REAL)
			returnValue.set(new SpeacReal(Math.abs(((SpeacReal) arguments.get(0)).value())));
		else
			return new SpeacPartialError(new CoreModuleErrors.InvalidArgumentTypeL(0, Maths.NUMBER_TYPES, arguments.get(0).type()), "absolute value of");
		return null; // No errors
	};

	public static final CoreFunctionCallable IS_GREATER_THAN
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		double first, second;

		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			first = ((SpeacInteger) arguments.get(0)).value();
			second = ((SpeacInteger) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			first = ((SpeacInteger) arguments.get(0)).value();
			second = ((SpeacReal) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			first = ((SpeacReal) arguments.get(0)).value();
			second = ((SpeacInteger) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			first = ((SpeacReal) arguments.get(0)).value();
			second = ((SpeacReal) arguments.get(1)).value();

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"is greater than");

		returnValue.set(new SpeacBoolean(first > second));
		return null; // No errors
	};

	public static final CoreFunctionCallable IS_LESS_THAN
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		double first, second;

		if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			first = ((SpeacInteger) arguments.get(0)).value();
			second = ((SpeacInteger) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.INTEGER
				&& arguments.get(1).type() == Data.Type.REAL) {

			first = ((SpeacInteger) arguments.get(0)).value();
			second = ((SpeacReal) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.INTEGER) {

			first = ((SpeacReal) arguments.get(0)).value();
			second = ((SpeacInteger) arguments.get(1)).value();

		} else if (arguments.get(0).type() == Data.Type.REAL
				&& arguments.get(1).type() == Data.Type.REAL) {

			first = ((SpeacReal) arguments.get(0)).value();
			second = ((SpeacReal) arguments.get(1)).value();

		} else
			return new SpeacPartialError(
					new CoreModuleErrors.InvalidArgumentTypesCombination(Data.extractTypes(arguments)),
					"is less than");

		returnValue.set(new SpeacBoolean(first < second));
		return null; // No errors
	};

	public static final CoreFunctionCallable AND
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.BOOLEAN,
				Data.Type.BOOLEAN
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "and");

		boolean first = ((SpeacBoolean) arguments.get(0)).value();
		boolean second = ((SpeacBoolean) arguments.get(1)).value();
		returnValue.set(new SpeacBoolean(first && second));
		return null; // No errors
	};

	public static final CoreFunctionCallable OR
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.BOOLEAN,
				Data.Type.BOOLEAN
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "or");

		boolean first = ((SpeacBoolean) arguments.get(0)).value();
		boolean second = ((SpeacBoolean) arguments.get(1)).value();
		returnValue.set(new SpeacBoolean(first || second));
		return null; // No errors
	};

	public static final CoreFunctionCallable NOT
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.BOOLEAN
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "not");

		boolean input = ((SpeacBoolean) arguments.get(0)).value();
		returnValue.set(new SpeacBoolean(!input));
		return null; // No errors
	};

	public static final CoreFunctionCallable TRIMMED_TO_DECIMAL_PLACES
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.REAL,
				Data.Type.INTEGER
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "trimmed to decimal places");

		double toTrim = ((SpeacReal) arguments.get(0)).value();
		int places = ((SpeacInteger) arguments.get(1)).value();
		if (places < 1)
			if (places == 0)
				return new SpeacPartialError(CoreModuleErrors.TRIMMING_TO_ZERO_DECIMAL_PLACES, SpeacError.NO_TOKEN);
			else
				return new SpeacPartialError(CoreModuleErrors.TRIMMING_TO_NEGATIVE_DECIMAL_PLACES, SpeacError.NO_TOKEN);

		returnValue.set(new SpeacReal(MathUtils.trim(toTrim, places)));
		return null; // No errors
	};

	public static final CoreFunctionCallable ROUNDED_TO_DECIMAL_PLACES
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.REAL,
				Data.Type.INTEGER
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "rounded to decimal places");

		double toRound = ((SpeacReal) arguments.get(0)).value();
		int places = ((SpeacInteger) arguments.get(1)).value();
		if (places < 1)
			if (places == 0)
				return new SpeacPartialError(CoreModuleErrors.ROUNDING_TO_ZERO_DECIMAL_PLACES, SpeacError.NO_TOKEN);
			else
				return new SpeacPartialError(CoreModuleErrors.ROUNDING_TO_NEGATIVE_DECIMAL_PLACES, SpeacError.NO_TOKEN);

		returnValue.set(new SpeacReal(MathUtils.round(toRound, places)));
		return null; // No errors
	};

	public static final CoreFunctionCallable TRIMMED
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.REAL
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "trimmed");

		double toTrim = ((SpeacReal) arguments.get(0)).value();
		returnValue.set(new SpeacInteger((int) toTrim));
		return null; // No errors
	};

	public static final CoreFunctionCallable ROUNDED
			= (runner, arguments, scope, parentScope, breakLevel, returnValue) -> {
		SpeacError argumentsError = SpeacCoreModuleUtilities.validateArguments(arguments,
				Data.Type.REAL
		);
		if (argumentsError != null)
			return new SpeacPartialError(argumentsError, "rounded");

		double toRound = ((SpeacReal) arguments.get(0)).value();
		returnValue.set(new SpeacInteger((int) Math.round(toRound)));
		return null; // No errors
	};
}
