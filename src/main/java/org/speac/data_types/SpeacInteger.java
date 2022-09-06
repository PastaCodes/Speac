package org.speac.data_types;

import org.speac.core.types.language.Data;

public record SpeacInteger(Integer value) implements Data<Integer> {
	@Override public Integer value() {
		return this.value;
	}

	@Override public boolean matches(Data<?> other) {
		if (!(other instanceof SpeacInteger))
			return false;
		return this.value.equals(((SpeacInteger) other).value());
	}

	@Override public String toString(boolean printable) {
		return this.value.toString();
	}

	@Override public Type type() {
		return Type.INTEGER;
	}
}
