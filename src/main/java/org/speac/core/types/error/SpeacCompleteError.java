package org.speac.core.types.error;

/**
 * This error contains all possible elements (name, description, solution, line number, source, incriminated token)
 * Together with {@link SpeacPartialError} it implements {@link SpeacRaisedError}
 * @see SpeacRaisedError for more info
 */
public record SpeacCompleteError(SpeacError error, int lineNumber, String source, String token) implements SpeacRaisedError {
	/**
	 * In some cases it might be useful to add the line number and source to an already packaged {@link SpeacPartialError},
	 * which already contains the name, description, solution and incriminated token for the error
	 */
	public static SpeacCompleteError wrap(SpeacPartialError error, int lineNumber, String source) {
		return new SpeacCompleteError(error.error(), lineNumber, source, error.token());
	}
}
