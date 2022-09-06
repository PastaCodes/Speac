package org.speac.core.types.error;

/**
 * Adds the incriminated token to the information contained in a normal SpeacError (name, description, solution)
 * Together with {@link SpeacCompleteError} it implements {@link SpeacRaisedError}
 * @see SpeacRaisedError for more info
 */
public record SpeacPartialError(SpeacError error, String token) implements SpeacRaisedError {}
