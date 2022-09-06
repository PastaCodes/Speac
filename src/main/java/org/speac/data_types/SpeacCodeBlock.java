package org.speac.data_types;

import org.speac.core.types.language.CodeBlock;
import org.speac.core.types.language.Data;

public record SpeacCodeBlock(CodeBlock value) implements Data<CodeBlock> {
	@Override public CodeBlock value() {
		return this.value;
	}

	@Override public boolean matches(Data<?> other) {
		return false;
	}

	@Override public String toString(boolean printable) {
		return "Code Block (" + this.value.lines().size() + " lines)";
	}

	@Override public Type type() {
		return Type.CODE_BLOCK;
	}
}
