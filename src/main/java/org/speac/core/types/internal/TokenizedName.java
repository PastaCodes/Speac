package org.speac.core.types.internal;

import org.speac.utilities.FixedList;

/**
 * Because of the nature of this language it can be helpful to store the names of variables as lists of tokens
 * create < "my dummy variable" > as < 42 --> ["my", "dummy", "variable"]
 */
public record TokenizedName(FixedList<String> tokens) {
	public TokenizedName(String ... tokens) {
		this(new FixedList<>(tokens));
	}

	// Compares two names
	public boolean matches(TokenizedName other) {
		if (other.tokens.size() != this.tokens.size())
			return false;
		for (int tokenIndex = 0; tokenIndex < this.tokens.size(); tokenIndex++)
			if (!this.tokens.get(tokenIndex).equals(other.tokens.get(tokenIndex)))
				return false;
		return true;
	}

	@Override public String toString() {
		return String.join(" ", this.tokens);
	}
}
