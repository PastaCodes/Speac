package org.speac.core.types.internal;

public class Token {
	public enum Type {
		UNKNOWN,
		SYMBOL,
		FUNCTION_NAME,
		GENERIC_ARGUMENT,
		VARIABLE_NAME,
		IMMEDIATE_VALUE,
		PARENTHESES_EXPRESSION,
		STRING,
		STRING_ARGUMENT,
		ERROR
	}

	public String contents;
	public Type type;
	public int beginIndex;
	public int endIndex;

	public Token(String contents, Type type, int beginIndex, int endIndex) {
		this.contents = contents;
		this.type = type;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	public Token(Type type, int beginIndex) {
		this("", type, beginIndex, -1);
	}

	// Used for debugging
	@Override public String toString() {
		return this.contents;
	}
}
