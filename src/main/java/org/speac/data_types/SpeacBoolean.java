package org.speac.data_types;

import org.speac.core.types.language.Data;

public record SpeacBoolean(Boolean value) implements Data<Boolean> {
	@Override public Boolean value() {
		return this.value;
	}

	@Override public boolean matches(Data<?> other) {
		if (!(other instanceof SpeacBoolean))
			return false;
		return this.value == ((SpeacBoolean) other).value();
	}

	@Override public String toString(boolean printable) {
		return this.value ? "True" : "False";
	}

	@Override public Type type() {
		return Type.BOOLEAN;
	}
}
