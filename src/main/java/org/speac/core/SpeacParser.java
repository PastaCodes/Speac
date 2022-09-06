package org.speac.core;

import org.speac.core.types.error.SpeacError;
import org.speac.core.types.internal.Token;
import org.speac.core.types.internal.TokenizedName;
import org.speac.core.types.internal.VariadicPrototype;
import org.speac.core.types.language.Argument;
import org.speac.core.types.language.Prototype;
import org.speac.errors.SyntaxErrors;
import org.speac.utilities.FixedList;
import org.speac.utilities.ListUtils;
import org.speac.utilities.Reference;
import org.speac.utilities.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * The automaton responsible for parsing the Speac syntax
 * An in-depth look at the syntax is found in the README.md file
 */
public class SpeacParser {
	// The list of all possible states of the parsing automaton.
	private enum State {
		NORMAL,
		IN_PARENTHESES,
		IN_STRING,
		IN_STRING_IN_PARENTHESES,
		AFTER_PARENTHESES,
		AFTER_STRING
	}

	/**
	 * Splits an instruction into an array of tokens, returns an error if the syntax is incorrect.
	 *
	 * @param instruction the input string to be split
	 * @param tokens the resulting array of tokens
	 * @param ignoreEmptyArguments this method is not only used to parse instructions
	 *                             and sometimes it can be helpful to avoid raising an error
	 *                             when an empty argument is found
	 * @return the first encountered error or null if none was found
	 */
	private static SpeacError parse(String instruction, Reference<FixedList<Token>> tokens, boolean ignoreEmptyArguments) {
		State state = State.NORMAL; // Keeps track of the current state of the parser automaton
		int parenthesesDepth = 0; // Is used to handle nested parentheses
		Token currentToken = new Token(Token.Type.UNKNOWN, 0); // The current token that is being assembled
		LinkedList<Token> foundTokens = tokens.isSet()
				? new LinkedList<>(tokens.get())
				: new LinkedList<>();

		// Scan the instruction character by character, assemble tokens and add them when they are complete,
		// update the state of the automaton when needed
		for (int instructionIndex = 0; instructionIndex < instruction.length(); instructionIndex++) {
			char currentCharacter = instruction.charAt(instructionIndex);

			switch (state) {
				case IN_PARENTHESES:
					switch (currentCharacter) {
						case '(':
							parenthesesDepth++;
							currentToken.contents += '(';
							break;

						case ')':
							if (--parenthesesDepth == 0) {
								currentToken.endIndex = instructionIndex;
								state = State.AFTER_PARENTHESES;
							}
							currentToken.contents += ')';
							break;

						case '"':
							currentToken.contents += '"';
							state = State.IN_STRING_IN_PARENTHESES;
							break;

						default:
							// Simply store the characters, they will be put into a PARENTHESES_EXPRESSION token.
							// A deep, recursive parsing process can be obtained by applying the
							// solveParentheses method to the already parsed tokens
							currentToken.contents += currentCharacter;
							break;
					}
					break;

				case IN_STRING:
					switch (currentCharacter) {
						case '"':
							currentToken.contents += '"';
							currentToken.endIndex = instructionIndex;
							state = State.AFTER_STRING;
							break;

						default:
							currentToken.contents += currentCharacter;
							break;
					}
					break;

				// This state is needed because parentheses inside strings should not affect the nesting level
				case IN_STRING_IN_PARENTHESES:
					switch (currentCharacter) {
						case '"':
							currentToken.contents += '"';
							state = State.IN_PARENTHESES;
							break;

						default:
							currentToken.contents += currentCharacter;
							break;
					}
					break;

				case AFTER_PARENTHESES:
					switch (currentCharacter) {
						case ' ':
						case '\t':
							currentToken.contents += currentCharacter;
							break;
						case '(':
							return SyntaxErrors.UNEXPECTED_OPEN_PARENTHESIS;
						case ')':
							return SyntaxErrors.UNEXPECTED_CLOSED_PARENTHESIS;
						case '<':
							return SyntaxErrors.PARENTHESES_AS_FUNCTION;
						case '>':
							currentToken.type = Token.Type.PARENTHESES_EXPRESSION;
							foundTokens.add(currentToken);

							foundTokens.add(new Token(">", Token.Type.SYMBOL, instructionIndex, instructionIndex));

							currentToken = new Token(Token.Type.FUNCTION_NAME, instructionIndex + 1);
							state = State.NORMAL;
							break;
						default:
							return SyntaxErrors.UNEXPECTED_CHARACTERS_AFTER_PARENTHESES;
					}
					break;

				case AFTER_STRING:
					switch (currentCharacter) {
						case ' ':
						case '\t':
							currentToken.contents += currentCharacter;
							break;
						case '(':
							return SyntaxErrors.UNEXPECTED_OPEN_PARENTHESIS;
						case ')':
							return SyntaxErrors.UNEXPECTED_CLOSED_PARENTHESIS;
						case '<':
							return SyntaxErrors.STRING_AS_FUNCTION;
						case '>':
							currentToken.type = Token.Type.STRING;
							foundTokens.add(currentToken);

							foundTokens.add(new Token(">", Token.Type.SYMBOL, instructionIndex, instructionIndex));

							currentToken = new Token(Token.Type.FUNCTION_NAME, instructionIndex + 1);
							state = State.NORMAL;
							break;
						default:
							return SyntaxErrors.UNEXPECTED_CHARACTERS_AFTER_STRING;
					}
					break;

				case NORMAL:
					switch (currentCharacter) {
						case '(':
							if (!currentToken.contents.isBlank())
								return SyntaxErrors.UNEXPECTED_OPEN_PARENTHESIS;
							if (currentToken.type == Token.Type.FUNCTION_NAME)
								return SyntaxErrors.PARENTHESES_AS_FUNCTION;

							currentToken = new Token(currentToken.type, instructionIndex);
							currentToken.contents += '(';

							parenthesesDepth = 1;
							state = State.IN_PARENTHESES;
							break;

						case ')':
							return SyntaxErrors.UNEXPECTED_CLOSED_PARENTHESIS;

						case '"':
							if (!currentToken.contents.isBlank())
								return SyntaxErrors.UNEXPECTED_QUOTATION_MARKS;
							if (currentToken.type == Token.Type.FUNCTION_NAME)
								return SyntaxErrors.STRING_AS_FUNCTION;

							currentToken = new Token(currentToken.type, instructionIndex);
							currentToken.contents += '"';

							state = State.IN_STRING;
							break;

						case '<':
							if (currentToken.type == Token.Type.GENERIC_ARGUMENT)
								return SyntaxErrors.CONFLICTING_ROUTERS;

							if (currentToken.type == Token.Type.UNKNOWN)
								currentToken.type = Token.Type.FUNCTION_NAME;
							currentToken.endIndex = instructionIndex - 1;
							// if (!currentToken.contents.isEmpty())
							foundTokens.add(currentToken);

							foundTokens.add(new Token("<", Token.Type.SYMBOL, instructionIndex, instructionIndex));

							currentToken = new Token(Token.Type.GENERIC_ARGUMENT, instructionIndex + 1);
							break;

						case '>':
							if (currentToken.type == Token.Type.FUNCTION_NAME)
								return SyntaxErrors.CONFLICTING_ROUTERS;

							if (currentToken.type == Token.Type.UNKNOWN)
								currentToken.type = Token.Type.GENERIC_ARGUMENT;
							currentToken.endIndex = instructionIndex - 1;
							if (currentToken.contents.isBlank() && !ignoreEmptyArguments)
								return SyntaxErrors.EMPTY_ARGUMENT;
							foundTokens.add(currentToken);

							foundTokens.add(new Token(">", Token.Type.SYMBOL, instructionIndex, instructionIndex));

							currentToken = new Token(Token.Type.FUNCTION_NAME, instructionIndex + 1);
							break;

						default:
							currentToken.contents += currentCharacter;
							break;
					}
					break;
				default: break;
			}
		}

		switch (state) {
			case IN_PARENTHESES:
				return SyntaxErrors.MISSING_CLOSED_PARENTHESIS;
			case IN_STRING:
			case IN_STRING_IN_PARENTHESES:
				return SyntaxErrors.MISSING_QUOTATION_MARKS;
			case AFTER_PARENTHESES:
				if (currentToken.type == Token.Type.UNKNOWN || currentToken.type == Token.Type.FUNCTION_NAME)
					return SyntaxErrors.PARENTHESES_AS_FUNCTION;
				currentToken.type = Token.Type.PARENTHESES_EXPRESSION;
				break;
			case AFTER_STRING:
				if (currentToken.type == Token.Type.UNKNOWN || currentToken.type == Token.Type.FUNCTION_NAME)
					return SyntaxErrors.STRING_AS_FUNCTION;
				currentToken.type = Token.Type.STRING;
				break;
			default: break;
		}

		currentToken.endIndex = instruction.length() - 1;

		if (currentToken.type == Token.Type.UNKNOWN)
			currentToken.type = Token.Type.FUNCTION_NAME; // Assume a function call without arguments

		if (currentToken.type == Token.Type.GENERIC_ARGUMENT
				&& currentToken.contents.isBlank()
				&& !ignoreEmptyArguments
		)
			return SyntaxErrors.EMPTY_ARGUMENT;

		foundTokens.add(currentToken);

		// Check if no function tokens where found
		if (foundTokens.stream()
				.noneMatch(token ->
						token.type == Token.Type.FUNCTION_NAME
								&& !token.contents.isBlank()))
			return SyntaxErrors.EMPTY_FUNCTION;

		/*
		boolean emptyFunction = true;
		for (Token token : foundTokens)
			if (token.type == Token.Type.FUNCTION_NAME && !token.contents.isBlank()) {
				emptyFunction = false;
				break;
			}
		if (emptyFunction)
			return SyntaxErrors.EMPTY_FUNCTION;
		*/

		tokens.set(new FixedList<>(foundTokens)); // Store the resulting tokens
		return null; // No errors
	}

	/**
	 * Breaks up a variable name into its tokens
	 * For example:
	 * 		"my beautiful variable" -> ["my", "beautiful", "variable"]
	 */
	public static TokenizedName parseVariableName(String base) {
		// TODO check whether this alternative is valid
		return new TokenizedName(base.split(" "));
		/*
		Reference<Prototype> variableName = new Reference<>();
		Reference<Argument[]> noArguments = new Reference<>();
		SpeacError parseError = SpeacParser.parseInstruction(base, variableName, noArguments);
		if (parseError != null || noArguments.get().length > 0)
			return null;
		return variableName.get().name;
		*/
	}

	/**
	 * Extrapolates the variadic prototype out of a string
	 * For example:
	 * 		"list with ..." -> VariadicPrototype(name = ["list", "with"])
	 */
	public static VariadicPrototype parseVariadicPrototype(String base) {
		String trimmed = base.trim();
		if (base.contains("<") || base.contains(">") || !trimmed.endsWith("..."))
			return null;
		return new VariadicPrototype(SpeacParser.parseVariableName(
				trimmed.substring(0, trimmed.length() - "...".length())
		));
	}

	/**
	 * Parses a prototype without its parameters
	 * For example:
	 * 		"repeat < > times <" -> Prototype(name = ["repeat", "times"], argumentPositions = [1, 2])
	 */
	public static Prototype parseEmptyPrototype(String base) {
		VariadicPrototype prototype = SpeacParser.parseVariadicPrototype(base);
		if (prototype != null)
			return prototype;

		Reference<FixedList<Token>> tokens = new Reference<>(); // Tokens extracted while parsing
		SpeacError parseError = SpeacParser.parse(base, tokens, true);
		if (parseError != null)
			return null;

		LinkedList<String> foundFunctionTokens = new LinkedList<>();
		LinkedList<Integer> foundArgumentPositions = new LinkedList<>();

		for (Token currentToken : tokens.get()) {
			switch (currentToken.type) {
				case FUNCTION_NAME:
					foundFunctionTokens.addAll(Arrays.asList(currentToken.contents.trim().split(" +")));
					break;
				case GENERIC_ARGUMENT:
					foundArgumentPositions.add(foundFunctionTokens.size());
					break;
				case PARENTHESES_EXPRESSION:
				case STRING:
					return null;
				default:
					break;
			}
		}

		return new Prototype(new TokenizedName(
				new FixedList<>(foundFunctionTokens)),
				new FixedList<>(foundArgumentPositions)
		);
	}

	public static Prototype parsePrototype(String base, Reference<FixedList<Argument>> parameters) {
		VariadicPrototype prototype = parseVariadicPrototype(base);
		if (prototype != null)
			return prototype;

		Reference<Prototype> functionName = new Reference<>();
		if (SpeacParser.parseInstruction(base, functionName, parameters) != null)
			return null;
		return functionName.get();
	}

	/**
	 * Extracts information from an instruction using the tokens from {@link SpeacParser#parse(String, Reference, boolean)}.
	 * @param functionName the name of the function called within the instruction
	 * @param arguments the arguments extracted from the instruction
	 * @return the first error encountered while parsing or null if none was found
	 */
	public static SpeacError parseInstruction(
			String instruction,
			Reference<Prototype> functionName,
			Reference<FixedList<Argument>> arguments
	) {
		Reference<FixedList<Token>> tokens = new Reference<>(); // Tokens extracted while parsing
		SpeacError parseError = SpeacParser.parse(instruction, tokens, false);
		if (parseError != null)
			return parseError;

		for (Token token : tokens.get())
			token.contents = token.contents.trim();

		LinkedList<String> foundFunctionTokens = new LinkedList<>();
		LinkedList<Argument> foundArgumentTokens = arguments.isSet()
				? new LinkedList<>(arguments.get())
				: new LinkedList<>();
		LinkedList<Integer> foundArgumentPositions = new LinkedList<>();

		for (Token currentToken : tokens.get()) {
			switch (currentToken.type) {
				case FUNCTION_NAME:
					foundFunctionTokens.addAll(Arrays.asList(currentToken.contents.trim().split(" +")));
					break;
				case GENERIC_ARGUMENT:
					foundArgumentTokens.add(new Argument(
							currentToken.contents,
							Argument.Type.VARIABLE
					));
					foundArgumentPositions.add(foundFunctionTokens.size());
					break;
				case PARENTHESES_EXPRESSION:
					foundArgumentTokens.add(new Argument(
							currentToken.contents.substring(1, currentToken.contents.length() - 1),
							Argument.Type.PARENTHESES_EXPRESSION
					));
					foundArgumentPositions.add(foundFunctionTokens.size());
					break;
				case STRING:
					foundArgumentTokens.add(new Argument(
							currentToken.contents.substring(1, currentToken.contents.length() - 1),
							Argument.Type.STRING
					));
					foundArgumentPositions.add(foundFunctionTokens.size());
					break;
				default:
					break;
			}
		}

		// Store the results
		functionName.set(new Prototype(new TokenizedName(
				new FixedList<>(foundFunctionTokens)),
				new FixedList<>(foundArgumentPositions)
		));
		arguments.set(new FixedList<>(foundArgumentTokens));

		return null; // No errors
	}

	/**
	 * Indicates what purpose each part of the instruction has. Most likely used for syntax highlighting.
	 * @param foundTokens list of resulting tokens
	 * @return the first error encountered while parsing or null if none was found
	 */
	public static SpeacError parseSyntax(String instruction, Reference<FixedList<Token>> foundTokens) {
		SpeacError parseError = SpeacParser.parse(instruction, foundTokens, false);
		if (parseError != null)
			return parseError;
		parseError = SpeacParser.solveParentheses(foundTokens);
		if (parseError != null)
			return parseError;
		SpeacParser.solveStrings(foundTokens);
		SpeacParser.detectImmediateValues(foundTokens.get());
		return null; // No errors
	}

	/**
	 * If no syntax error is present,
	 * it returns the same thing that {@link SpeacParser#parseSyntax(String, Reference)} puts into foundTokens
	 * otherwise it returns a big token containing the whole instruction and with token type error
	 */
	public static FixedList<Token> parseSyntaxOrError(String instruction) {
		Reference<FixedList<Token>> foundTokens = new Reference<>();
		SpeacError parseError = SpeacParser.parseSyntax(instruction, foundTokens);
		if (parseError != null)
			return new FixedList<>(new Token(instruction, Token.Type.ERROR, 0, instruction.length() - 1));
		return foundTokens.get();
	}

	/**
	 * Parses the instructions found between parentheses recursively.
	 * @param foundTokens list of tokens to be modified
	 * @return the first error encountered while parsing or null if none was found
	 */
	private static SpeacError solveParentheses(Reference<FixedList<Token>> foundTokens) {
		LinkedList<Token> resultTokens = new LinkedList<>(foundTokens.get());
		// tokenIndex is modified
		for (int tokenIndex = 0; tokenIndex < resultTokens.size(); tokenIndex++) {
			Token currentToken = resultTokens.get(tokenIndex);

			if (currentToken.type == Token.Type.PARENTHESES_EXPRESSION) {
				String innerInstruction = currentToken.contents.trim();
				innerInstruction = innerInstruction.substring(1, innerInstruction.length() - 1); // remove parentheses
				Reference<FixedList<Token>> innerTokens = new Reference<>(new FixedList<>());

				ListUtils.appendToFixedListReference(innerTokens,
						new Token("(", Token.Type.SYMBOL, -1, -1));

				SpeacError parseError = SpeacParser.parse(innerInstruction, innerTokens, false);
				if (parseError != null) return parseError;
				parseError = SpeacParser.solveParentheses(innerTokens);
				if (parseError != null) return parseError;

				ListUtils.appendToFixedListReference(innerTokens,
						new Token(")", Token.Type.SYMBOL, innerInstruction.length(), innerInstruction.length()));

				for (Token innerToken : innerTokens.get()) {
					innerToken.beginIndex += currentToken.beginIndex + 1;
					innerToken.endIndex += currentToken.beginIndex + 1;
				}

				resultTokens.remove(tokenIndex);
				resultTokens.addAll(tokenIndex, innerTokens.get());
				tokenIndex += innerTokens.get().size() /**/- 1/* the for loop increases it again */;
			}
		}
		foundTokens.set(new FixedList<>(resultTokens));
		return null; // No errors
	}

	/**
	 * Parses any instruction inside string tokens. This is useful for syntax highlighting in function prototypes.
	 * No error needs to be ever returned because strings that don't follow Speac syntax simply remain untouched.
	 * @param foundTokens list of tokens to be modified
	 */
	private static void solveStrings(Reference<FixedList<Token>> foundTokens) {
		LinkedList<Token> resultTokens = new LinkedList<>(foundTokens.get());
		// tokenIndex is modified
		for (int tokenIndex = 0; tokenIndex < resultTokens.size(); tokenIndex++) {
			Token currentToken = resultTokens.get(tokenIndex);

			if (currentToken.type == Token.Type.STRING) {
				String innerInstruction = currentToken.contents.trim();
				innerInstruction = innerInstruction.substring(1, innerInstruction.length() - 1); // remove quotation marks
				Reference<FixedList<Token>> innerTokens = new Reference<>(new FixedList<>());

				ListUtils.appendToFixedListReference(innerTokens,
						new Token("\"", Token.Type.SYMBOL, -1, -1));

				if (SpeacParser.parse(innerInstruction, innerTokens, false) != null) continue;

				ListUtils.appendToFixedListReference(innerTokens,
						new Token("\"", Token.Type.STRING, innerInstruction.length(), innerInstruction.length()));

				for (Token innerToken : innerTokens.get()) {
					innerToken.beginIndex += currentToken.beginIndex + 1;
					innerToken.endIndex += currentToken.beginIndex + 1;
					if (innerToken.type == Token.Type.GENERIC_ARGUMENT)
						innerToken.type = Token.Type.STRING_ARGUMENT;
					else
						innerToken.type = Token.Type.STRING;
				}

				resultTokens.remove(tokenIndex);
				resultTokens.addAll(tokenIndex, innerTokens.get());
				tokenIndex += innerTokens.get().size() /**/- 1/* the for loop increases it again */;
			}
		}
		foundTokens.set(new FixedList<>(resultTokens));
	}

	/**
	 * Highlights differently the immediate values (i.g. integers and reals).
	 * @param tokens array of tokens to be modified, doesn't need to be a reference because its length won't change
	 */
	private static void detectImmediateValues(FixedList<Token> tokens) {
		for (Token currentToken : tokens) {
			if (currentToken.type == Token.Type.GENERIC_ARGUMENT)
				if (
						StringUtils.stringMatchesDouble(currentToken.contents) ||
								StringUtils.stringMatchesInteger(currentToken.contents)
				)
					currentToken.type = Token.Type.IMMEDIATE_VALUE;
				else
					currentToken.type = Token.Type.VARIABLE_NAME;
		}
	}
}
