package org.speac.data_types;

import org.speac.core.types.language.Data;

public record SpeacEmpty() implements Data<Object> {
	@Override public Object value() {
		return null;
	}

	@Override public boolean matches(Data<?> other) {
		return other instanceof SpeacEmpty;
	}

	@Override public String toString(boolean printable) {
		return "Empty";
	}

	@Override public Type type() {
		return Type.EMPTY;
	}
}
