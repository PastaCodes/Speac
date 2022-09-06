package org.speac.core.types.internal;

import org.speac.core.types.error.SpeacError;

import java.util.Scanner;

/**
 * This wrapper uses the system command line prompt. It is not the default wrapper because the
 * CLI might not be easy to understand for inexperienced users.
 */
public class SpeacCommandLineWrapper implements SpeacWrapper {
	public static class NoTerminalException extends Exception {
		public NoTerminalException() {
			super("No output terminal available");
		}
	}

	private final Scanner scanner; // Used to read input from the user
	private int localLineTracker; // Used to keep track of the current line number

	public SpeacCommandLineWrapper() throws NoTerminalException {
		if (System.out == null)
			throw new NoTerminalException();
		this.scanner = new Scanner(System.in);
		this.localLineTracker = 1;
	}

	@Override public void consoleOut(String line) {
		System.out.println("     -> " + line);
	}

	@Override public String consoleIn() {
		System.out.print("     <- ");
		return this.scanner.nextLine();
	}

	@Override public void errorOut(
			String name,        String description,
			String solution,    int lineNumber,
			String source,      String incriminatedToken) {
		String errorTitle = " !!! -> ";

		errorTitle += "Error";
		if (lineNumber != SpeacError.NO_LINE_NUMBER)
			errorTitle += " in line " + lineNumber;
		if (source != SpeacError.TERMINAL_SOURCE)
			errorTitle += " of " + source;
		errorTitle += ": ";
		if (name != SpeacError.NO_NAME)
			errorTitle += name;
		else
			errorTitle += "Unhandled Error";

		System.out.println(errorTitle);

		if (incriminatedToken != SpeacError.NO_TOKEN)
			System.out.println("        Incriminated Token: '" + incriminatedToken + "'.");

		if (description != SpeacError.NO_DESCRIPTION)
			System.out.println("        Description: " + description);
		else
			System.out.println("        No further description.");

		if (solution != SpeacError.NO_SOLUTION)
			System.out.println("        Suggested Solution: " + solution);
		else
			System.out.println("        No suggested solution available.");
	}

	@Override public String instructionIn() {
		// Lines past 999 are currently not supported
		System.out.print(" " + String.format("%03d", this.localLineTracker++ % 1000) + " <- ");
		return this.scanner.nextLine();
	}

	@Override public void pause() {
		System.out.print("     <- Press any key to continue ");
		this.scanner.nextLine();
	}
}

