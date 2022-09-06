package org.speac.errors;

import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacStaticError;

/**
 * @see org.speac.core.SpeacParser for a detailed overview of the Speac syntax
 */
public final class SyntaxErrors {
	/**
	 * Example:     create < > as < 42
	 *                     ^^^
	 */
	public static final SpeacStaticError EMPTY_ARGUMENT = new SpeacStaticError(
			"Empty Argument",
			SpeacError.NO_DESCRIPTION,
			"Please supply an argument or consider using and argument of type 'None'."
	);
	/**
	 * Example:     < 42 >
	 *              ^^^^^^
	 * A function must be called in every instruction
	 */
	public static final SpeacStaticError EMPTY_FUNCTION = new SpeacStaticError(
			"Empty Function",
			"No function name was specified in the current instruction.",
			"Please specify a function or consider using the 'rest' function from the 'Basics' core module."
	);
	/**
	 * Example:     a < b < c
	 *                ^   ^
	 * In this case the first router character indicates that b is an argument
	 * while the second router character indicates that it should be part of the function name
	 */
	public static final SpeacStaticError CONFLICTING_ROUTERS = new SpeacStaticError(
			"Conflicting Router Characters",
			SpeacError.NO_DESCRIPTION,
			"Please check for any mistakes or consider using parentheses."
	);
	/**
	 * Example:     say < (4 > plus < 2
	 *                                 ^
	 */
	public static final SpeacStaticError MISSING_CLOSED_PARENTHESIS = new SpeacStaticError(
			"Missing Closed Parenthesis",
			"More open parentheses than closed parentheses were found.",
			"Please add a closed parenthesis or check for an extra open parenthesis."
	);
	/**
	 * Example:     say < "Hello, world!
	 *                                  ^
	 */
	public static final SpeacStaticError MISSING_QUOTATION_MARKS = new SpeacStaticError(
			"Missing Quotation Marks",
			"The instruction ended without closing the string.",
			"Please add ending quotation marks or check for extra ones."
	);
	/**
	 * Example:     (4 > plus < 2) < 42
	 *              ^^^^^^^^^^^^^^^^
	 * The last router character seems to indicate that the instruction between the parentheses somehow represents
	 * the name of the function that needs to be called
	 */
	public static final SpeacStaticError PARENTHESES_AS_FUNCTION = new SpeacStaticError(
			"Interpreting Parentheses Expression As Function",
			SpeacError.NO_DESCRIPTION,
			"If you are trying to call a function dynamically please consider code block variables instead, otherwise check for a wrong router character."
	);
	/**
	 * Example:     "Hello, world!" < 42
	 *              ^^^^^^^^^^^^^^^^^
	 * The last router character seems to indicate that the string "Hello, world!" somehow represents
	 * the name of the function that needs to be called
	 */
	public static final SpeacStaticError STRING_AS_FUNCTION = new SpeacStaticError(
			"Interpreting String As Function",
			SpeacError.NO_DESCRIPTION,
			"If you are trying to call a function dynamically please consider code block variables instead, otherwise check for a wrong router character."
	);
	/**
	 * Example:     say < (4 > plus < 2) lorem ipsum
	 *                                   ^^^^^^^^^^^
	 */
	public static final SpeacStaticError UNEXPECTED_CHARACTERS_AFTER_PARENTHESES = new SpeacStaticError(
			"Unexpected Characters After Parentheses Expression",
			SpeacError.NO_DESCRIPTION,
			"Please remove these characters or check for a missing router character."
	);
	/**
	 * Example:     say < "Hello, world!" lorem ipsum
	 *                                    ^^^^^^^^^^^
	 */
	public static final SpeacStaticError UNEXPECTED_CHARACTERS_AFTER_STRING = new SpeacStaticError(
			"Unexpected Characters After String",
			SpeacError.NO_DESCRIPTION,
			"Please remove these characters or check for a missing router character."
	);
	/**
	 * Example:     say < 42 )
	 *                       ^
	 */
	public static final SpeacStaticError UNEXPECTED_CLOSED_PARENTHESIS = new SpeacStaticError(
			"Unexpected Closed Parenthesis",
			SpeacError.NO_DESCRIPTION,
			"Please remove this parenthesis or check for a missing open parenthesis."
	);
	/**
	 * Example:     say ( < 42
	 *                  ^
	 */
	public static final SpeacStaticError UNEXPECTED_OPEN_PARENTHESIS = new SpeacStaticError(
			"Unexpected Open Parenthesis",
			SpeacError.NO_DESCRIPTION,
			"Please remove this parenthesis or check for a missing router character."
	);
	/**
	 * Example:     say " < 42
	 *                  ^
	 */
	public static final SpeacStaticError UNEXPECTED_QUOTATION_MARKS = new SpeacStaticError(
			"Unexpected Quotation Marks",
			SpeacError.NO_DESCRIPTION,
			"Please remove these quotation marks or check for a missing router character."
	);
}
