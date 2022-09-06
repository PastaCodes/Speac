package org.speac.core;

import org.speac.Start;
import org.speac.core.types.error.SpeacCompleteError;
import org.speac.core.types.error.SpeacError;
import org.speac.core.types.error.SpeacPartialError;
import org.speac.core.types.error.SpeacRaisedError;
import org.speac.core.types.internal.*;
import org.speac.core.types.language.*;
import org.speac.data_types.*;
import org.speac.errors.CoreErrors;
import org.speac.native_module.FunctionsHandling;
import org.speac.native_module.SpeacNativeCoreModule;
import org.speac.utilities.FixedList;
import org.speac.utilities.Reference;
import org.speac.utilities.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Manages the execution of Speac code by requesting lines of code from a stream such as a code block or a terminal
 */
public class SpeacRunner {
	public final SpeacWrapper mainWrapper; // Wrapper responsible for user input
	private final ArrayList<SpeacWrapper> additionalWrappers; // List of wrappers to which any output should be forwarded

	public SpeacRunner(SpeacWrapper mainWrapper) {
		this.mainWrapper = mainWrapper;
		this.additionalWrappers = new ArrayList<>();
	}

	public void addWrapper(SpeacWrapper wrapper) {
		this.additionalWrappers.add(wrapper);
	}

	/**
	 * Main procedure to run code
	 *
	 * @param stream the source of lines of code
	 * @param scope the scope to run the code within
	 * @param parentScope the parent scope of the current scope
	 * @param breakLevel is changed if the break function is called
	 * @param raised is modified if an error is raised
	 * @return true if execution was stopped because the stream was broken, false otherwise (finished successfully, returned, or error encountered)
	 */
	private boolean runStream(
			LineStream stream,	Scope scope,
			Scope parentScope,	BreakLevel breakLevel,
			Reference<SpeacCompleteError> raised
	) {
		Line storedLine = null;
		LinkedList<Line> newBlockLines = null;

		while (true) {
			Line currentLine = stream.supply();

			if (currentLine != null) {
				if (currentLine.contents().isBlank())
					continue;

				switch (currentLine.contents().charAt(0)) {
					case ' ' -> {
						raised.set(new SpeacCompleteError(CoreErrors.UNEXPECTED_INDENTATION,
								currentLine.lineNumber(), currentLine.source(), SpeacError.NO_TOKEN));
						return false; // Error
					}
					case '\t' -> {
						if (storedLine == null) {
							raised.set(new SpeacCompleteError(CoreErrors.UNEXPECTED_INDENTATION,
									currentLine.lineNumber(), currentLine.source(), SpeacError.NO_TOKEN));
							return false; // Error
						}

						if (newBlockLines == null)
							newBlockLines = new LinkedList<>();

						newBlockLines.add(new Line(
								currentLine.contents().substring(1),
								currentLine.lineNumber(),
								currentLine.source()));
						continue;
					}
				}
			}

			if (storedLine != null) {
				SpeacRaisedError lineError = this.runInstruction(
						storedLine.contents(),	newBlockLines == null ? null : new FixedList<>(newBlockLines),
						scope,                  parentScope,
						breakLevel,				null);
				if (lineError != null) {
					if (lineError instanceof SpeacCompleteError completeError) {
						raised.set(completeError);
					} else if (lineError instanceof SpeacPartialError partialError) {
						raised.set(SpeacCompleteError.wrap(partialError,
								storedLine.lineNumber(), storedLine.source()));
					}
					return false; // Error
				}

				if (breakLevel.get() != 0) {
					if (breakLevel.get() > 0) {
						breakLevel.consume();
						return true; // Broken
					} else /* if (breakLevel.get() == BreakLevel.RETURNED) */
						return false; // Returned
				}

				newBlockLines = null;
			}

			if (currentLine == null)
				return false; // Finished

			storedLine = currentLine;
		}
	}

	/**
	 * Procedure responsible for executing a single instruction
	 *
	 * @param instruction input code to be executed
	 * @param codeBlockArgument the code block passed as an argument to the line
	 * @param returnValue the slot in which to store the instruction's return value, can be a dummy object
	 *                    but shouldn't be null
	 * @return the first error encountered while running the instruction or null if none was found
	 */
	private SpeacRaisedError runInstruction(
			String instruction,     FixedList<Line> codeBlockArgument,
			Scope scope,            Scope parentScope,
			BreakLevel breakLevel,	Reference<Data<?>> returnValue
	) {
		Reference<Prototype> functionName = new Reference<>();
		Reference<FixedList<Argument>> unloadedArguments = new Reference<>();

		// Parse the line
		SpeacError parseError = SpeacParser.parseInstruction(instruction, functionName, unloadedArguments);
		if (parseError != null)
			return new SpeacPartialError(parseError, instruction);

		// Evaluate the arguments
		Reference<FixedList<Data<?>>> loadedArguments = new Reference<>();
		SpeacRaisedError argumentsError = this.evaluateArguments(
				unloadedArguments.get(),    loadedArguments,
				scope,                      parentScope,
				breakLevel
		);
		if (argumentsError != null)
			return argumentsError;

		// Makes all needed changes if a code block argument is present
		SpeacRunner.handleCodeBlockArgument(codeBlockArgument, loadedArguments, functionName, scope);

		// Call the function; any errors (or the lack there of) should be carried over
		return this.callFunction(
				functionName.get(), loadedArguments.get(),
				scope,              parentScope,
				breakLevel,         returnValue
		);
	}

	/**
	 * Fetches the data associated with the arguments
	 *
	 * @param unloadedArguments source arguments in string form
	 * @param loadedArguments resulting data
	 * @return the first error encountered while fetching data or null if none was found
	 */
	private SpeacRaisedError evaluateArguments(
			FixedList<Argument> unloadedArguments,	Reference<FixedList<Data<?>>> loadedArguments,
			Scope scope,                    		Scope parentScope,
			BreakLevel breakLevel
	) {
		LinkedList<Data<?>> loadingArguments = new LinkedList<>();	// The fetched data, will be turned into an array
																	// and be stored in loadedArguments in the end
		for (Argument argument : unloadedArguments) {
			switch (argument.type()) {
				case PARENTHESES_EXPRESSION:
					// Need to evaluate the return value of the instruction inside the parentheses
					Reference<Data<?>> returnValue = new Reference<>(new SpeacEmpty());
					SpeacRaisedError innerError = this.runInstruction(
							argument.contents(),	null,
							scope,              	parentScope,
							breakLevel,         	returnValue
					);
					if (innerError != null)
						return innerError;

					loadingArguments.add(returnValue.get());
					break;

				case STRING:
					loadingArguments.add(new SpeacString(argument.contents()));
					break;

				case VARIABLE:
					// Check if the argument matches an immediate value
					if (StringUtils.stringMatchesInteger(argument.contents())) {
						loadingArguments.add(new SpeacInteger(Integer.parseInt(argument.contents())));
					} else if (StringUtils.stringMatchesDouble(argument.contents())) {
						loadingArguments.add(new SpeacReal(Double.parseDouble(argument.contents())));
					} else {
						// Otherwise look for the name amongst defined variables
						Data<?> variableValue = scope.accessVariable(SpeacParser.parseVariableName(argument.contents()));
						if (variableValue == null)
							return new SpeacPartialError(CoreErrors.UNDEFINED_VARIABLE, argument.contents());
						loadingArguments.add(variableValue); // variableValue.duplicate()
					}
					break;
			}
		}

		// Stores the data
		loadedArguments.set(new FixedList<>(loadingArguments));
		return null; // No errors
	}

	/**
	 * Applies all needed changes if a code block argument is present.
	 * If codeBlockArgument is null none of the referenced arguments will be modified
	 *
	 * @param codeBlockArgument checked if null and gets appended to the arguments if it isn't
	 * @param arguments the code block data is added
	 * @param functionName an empty token ("") is added if the argument positions suggested that
	 *                     the prototype ended with an argument
	 *                     Example:         'define <' --> 'define <> <'
	 *                                                               ^
	 *                     Counterexample:  'repeat <> times' --> 'repeat <> times <'
	 * @param scope when the code block is appended to the arguments it needs access to the scope in order to
	 *              save an image of its seen scope
	 */
	private static void handleCodeBlockArgument(
			FixedList<Line> codeBlockArgument,
			Reference<FixedList<Data<?>>> arguments, Reference<Prototype> functionName,
			Scope scope
	) {
		if (codeBlockArgument != null) {
			LinkedList<Data<?>> newArguments = new LinkedList<>(arguments.get());
			newArguments.add(new SpeacCodeBlock(new CodeBlock(codeBlockArgument, scope.hereditate())));
			arguments.set(new FixedList<>(newArguments));

			LinkedList<String> newFunctionTokens = new LinkedList<>(functionName.get().name.tokens());
			if (!functionName.get().argumentPositions.isEmpty()
					&& functionName.get().argumentPositions.getLast()
							== functionName.get().name.tokens().size())
				newFunctionTokens.add("");

			LinkedList<Integer> newArgumentPositions = new LinkedList<>(functionName.get().argumentPositions);
			newArgumentPositions.add(newFunctionTokens.size());

			functionName.set(new Prototype(new TokenizedName(
					new FixedList<>(newFunctionTokens)),
					new FixedList<>(newArgumentPositions)
			));
		}
	}

	/**
	 * @param prototype the prototype that identifies the function to be called
	 * @param returnValue the slot in which to store the instruction's return value
	 * @return the first error encountered while calling the function or null if none was found
	 */
	private SpeacRaisedError callFunction(
			Prototype prototype,    FixedList<Data<?>> arguments,
			Scope scope,            Scope parentScope,
			BreakLevel breakLevel,  Reference<Data<?>> returnValue
	) {
		Reference<FixedList<Integer>> argumentsCorrespondence = new Reference<>();
		Function function = scope.fetchFunction(prototype, argumentsCorrespondence);
		if (function == null)
			// Function name is not recognised
			return new SpeacPartialError(CoreErrors.UNDEFINED_FUNCTION, prototype.toString());

		arguments = SpeacRunner.shuffleArguments(arguments, argumentsCorrespondence.get());

		if (function instanceof CoreFunction coreFunction) {
			try {
				return coreFunction.callable.call(this, arguments, scope, parentScope, breakLevel, returnValue);
			} catch (Exception exception) {
				exception.printStackTrace();
				return new SpeacPartialError(CoreErrors.CORE_MODULE_EXCEPTION, SpeacError.NO_TOKEN);
			}
		} else if (function instanceof SpeacFunction speacFunction) {
			if (argumentsCorrespondence.get() == VariadicAlias.VARIADIC_CORRESPONDENCE)
				speacFunction.body.scope().addVariable(
						new Variable(new TokenizedName("..."), new SpeacList(arguments)));
			else
				// Make variables inside the function's body with the names of the parameters
				// and the values of the arguments
				FixedList.zip(speacFunction.parameters, arguments,
						(name, value) -> speacFunction.body.scope().addVariable(new Variable(name, value)));
				/*
				for (int parameterIndex = 0; parameterIndex < speacFunction.parameters.size(); parameterIndex++)
					speacFunction.body.scope().addVariable(new Variable(
							speacFunction.parameters.get(parameterIndex),
							arguments.get(parameterIndex)
					));
				*/

			Scope blockScope = speacFunction.body.scope().hereditate(); // We want to preserve the scope
			Reference<SpeacCompleteError> blockRaised = new Reference<>();
			this.runCodeBlock(speacFunction.body.lines(), blockScope, scope, breakLevel, blockRaised, true);
			if (blockRaised.isSet())
				return blockRaised.get();

			Data<?> functionReturnValue = blockScope.accessSystemVariable(
					FunctionsHandling.RETURN_VALUE_IDENTIFIER);
			if (functionReturnValue != null)
				returnValue.set(functionReturnValue);
		}
		return null; // Shouldn't happen
	}

	/**
	 * @param source an array of arguments to be shuffled
	 * @param indexes correspondence to follow while shuffling
	 * @return the shuffled array
	 *
	 * Example:
	 * 		source = ["i", "like", "pizza"], indexes = [1, 2, 0] --> ["pizza", "i", "like"]
	 */
	private static FixedList<Data<?>> shuffleArguments(FixedList<Data<?>> source, FixedList<Integer> indexes) {
		if (indexes == VariadicAlias.VARIADIC_CORRESPONDENCE)
			return source;

		Data<?>[] result = new Data<?>[source.size()];
		for (int index = 0; index < source.size(); index++)
			result[indexes.get(index)] = source.get(index);
		return new FixedList<>(result);
	}

	public void runTerminal() {
		Scope scope = new Scope();
		SpeacRunner.importInternalCoreModule(scope);
		LineStream terminalStream = new TerminalLineStream(this.mainWrapper);
		Reference<SpeacCompleteError> raised = new Reference<>();
		while (true) {
			boolean broken = this.runStream(terminalStream, scope, null, new BreakLevel(), raised);
			if (raised.isSet())
				this.wrappersErrorOut(raised.get());
			if (broken)
				break;
		}
	}

	/**
	 * @return same as {@link SpeacRunner#runStream(LineStream, Scope, Scope, BreakLevel, Reference)}
	 */
	public boolean runCodeBlock(
			FixedList<Line> lines,
			Scope scope,			Scope parentScope,
			BreakLevel breakLevel,	Reference<SpeacCompleteError> raised,
			boolean isBody
	) {
		boolean broken = this.runStream(new CodeBlockLineStream(lines), scope, parentScope, breakLevel, raised);
		if (parentScope != null) {
			SystemVariable<Data<?>> returnValue = scope.fetchSystemVariable(FunctionsHandling.RETURN_VALUE_IDENTIFIER);
			if (returnValue != null)
				if (isBody)
					breakLevel.reset();
				else
					parentScope.addSystemVariable(returnValue);
		}
		return broken;
	}
	public boolean runCodeBlock(
			FixedList<Line> lines,
			Scope scope,			Scope parentScope,
			BreakLevel breakLevel,	Reference<SpeacCompleteError> raised
	) {
		return this.runCodeBlock(lines, scope, parentScope, breakLevel, raised, false);
	}

	public void runScript(Path path) {
		FixedList<Line> scriptCode = Line.readLinesFromScript(path, true);
		if (scriptCode == null)
			return; // Couldn't find or read the script
		Scope scope = new Scope();
		SpeacRunner.importInternalCoreModule(scope);
		Reference<SpeacCompleteError> raised = new Reference<>();
		this.runCodeBlock(scriptCode, scope, null, new BreakLevel(), raised);
		if (raised.isSet())
			this.wrappersErrorOut(raised.get());
	}

	private static void importInternalCoreModule(Scope scope) {
		new SpeacNativeCoreModule().load(scope);
	}

	public boolean importCoreModule(String name, Scope scope, Reference<SpeacCompleteError> raised) {
		// TODO
		return false;
	}

	public boolean importModule(String name, Scope scope, Reference<SpeacCompleteError> raised) {
		FixedList<Line> moduleCode = Line.readLinesFromScript(Start.RESOURCES_DIRECTORY.resolve("modules").resolve(name + ".sp"), false);
		if (moduleCode == null)
			return false;
		Scope moduleScope = new Scope();
		SpeacRunner.importInternalCoreModule(moduleScope);
		this.runCodeBlock(moduleCode, moduleScope, null, new BreakLevel(), raised);
		Scope.merge(scope, moduleScope);
		return true;
	}

	// // // // // // // // // //
	//	Propagate output
	//

	public void wrappersConsoleOut(String line) {
		this.mainWrapper.consoleOut(line);
		for (SpeacWrapper wrapper : this.additionalWrappers)
			wrapper.consoleOut(line);
	}

	private void wrappersErrorOut(SpeacCompleteError error) {
		this.mainWrapper.errorOut(
				error.error().name(),        error.error().description(),
				error.error().solution(),    error.lineNumber(),
				error.source(),                 error.token());
		for (SpeacWrapper wrapper : this.additionalWrappers)
			wrapper.errorOut(
					error.error().name(),        error.error().description(),
					error.error().solution(),    error.lineNumber(),
					error.source(),                 error.token());
	}

	public void wrappersPause() {
		this.mainWrapper.pause();
		for (SpeacWrapper wrapper : this.additionalWrappers)
			wrapper.pause();
	}

	//
	// // // // // // // // // //
}
