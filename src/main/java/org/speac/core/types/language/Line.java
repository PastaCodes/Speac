package org.speac.core.types.language;

import org.speac.core.types.error.SpeacError;
import org.speac.utilities.FixedList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Defines the logical implementation of a line of code
 * Apart from the contents, it stored the line number and the source of line
 * @param source is either the path to the file from which this line was read or {@link SpeacError#TERMINAL_SOURCE}
 */
public record Line(String contents, int lineNumber, String source) {
	/**
	 * By default a line comes from the terminal
	 */
	public Line(String contents, int lineNumber) {
		this(contents, lineNumber, SpeacError.TERMINAL_SOURCE);
	}

	/**
	 * @param mainScript indicates whether the script is the one displayed in the terminal
	 *                   or an imported module, in the first case the lines will have as their source the scriptPath,
	 *                   while in the second case the lines will have the {@link SpeacError#TERMINAL_SOURCE}
	 */
	public static FixedList<Line> readLinesFromScript(Path scriptPath, boolean mainScript) {
		int lineTracker = 1;
		LinkedList<Line> lines = new LinkedList<>();
		try {
			Scanner scriptScanner = new Scanner(Files.newInputStream(scriptPath), StandardCharsets.UTF_8);

			while (scriptScanner.hasNextLine()) {
				lines.add(new Line(
						scriptScanner.nextLine(),
						lineTracker++,
						mainScript ? SpeacError.TERMINAL_SOURCE : scriptPath.toString()
				));
			}

			scriptScanner.close();
			return new FixedList<>(lines);

		} catch (IOException exception) {
			return null;
		}
	}

	// Used for debugging
	@Override public String toString() {
		return this.contents;
	}
}
