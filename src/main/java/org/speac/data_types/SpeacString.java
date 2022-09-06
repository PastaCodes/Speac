package org.speac.data_types;

import org.speac.core.types.language.Data;

public record SpeacString(String value) implements Data<String> {
	@Override public String value() {
		return this.value;
	}

	@Override public boolean matches(Data<?> other) {
		if (!(other instanceof SpeacString))
			return false;
		return this.value.equals(((SpeacString) other).value());
	}

	@Override public String toString(boolean printable) {
		if (printable)
			return this.value;
		else
			return '"' + this.value + '"';
	}

	@Override public Type type() {
		return Type.STRING;
	}
}
