package org.speac.core.types.language;

/**
 * Defines a variable that has the same structure of normal {@link Variable}'s
 * But which is invisible to the user and doesn't used fancy lists of tokenized names as identifiers
 * Instead it uses a simple empty object: {@link Identifier}
 */
public record SystemVariable<T extends Data<?>>(Identifier<T> identifier, T value) {
	public static class Identifier<E extends Data<?>> {}

	public boolean is(Identifier<? extends Data<?>> check) {
		return this.identifier == check;
	}
}
