package org.speac.data_types;

import org.speac.core.types.language.Data;

public record SpeacReal(Double value) implements Data<Double> {
	@Override public Double value() {
		return this.value;
	}

	@Override public boolean matches(Data<?> other) {
		if (!(other instanceof SpeacReal))
			return false;
		return this.value.equals(((SpeacReal) other).value());
	}

	@Override public String toString(boolean printable) {
		return this.value.toString();
	}

	@Override public Type type() {
		return Type.REAL;
	}
}
