package org.speac.core.types.language;

import org.speac.core.types.internal.Scope;
import org.speac.utilities.FixedList;

// A way to store some lines of code with their relative scope
public record CodeBlock(FixedList<Line> lines, Scope scope) {
	// Used for debugging
	@Override public String toString() {
		return "{%d lines}".formatted(this.lines.size());
	}
}
