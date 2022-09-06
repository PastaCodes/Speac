package org.speac.core.types.language;

// Used when parsing an instruction
public record Argument(String contents, Type type) {
	public enum Type {
		VARIABLE,
		PARENTHESES_EXPRESSION,
		STRING
	}

	// Used for debugging
	@Override public String toString() {
		return this.contents;
	}
}
