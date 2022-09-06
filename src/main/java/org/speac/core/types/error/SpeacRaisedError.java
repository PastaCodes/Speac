package org.speac.core.types.error;

/**
 * Defines the kinds of errors that can be raised by a line
 * Since it might invoke code from a code block it must be able to raise a {@link SpeacCompleteError}
 * And since it might raise an error itself it must be able to raise a {@link SpeacPartialError}
 * (lineNumber and source are not available to the line while it is executing)
 * In fact these are the only two implementations
 */
public interface SpeacRaisedError {}
