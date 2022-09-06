package org.speac.core.types.internal;

// A way to interface with the user
public interface SpeacWrapper {
	void consoleOut(String line);

	String consoleIn();

	void errorOut(
			String name,        String description,
			String solution,    int lineNumber,
			String source,      String incriminatedToken);

	String instructionIn();

	void pause();
}
