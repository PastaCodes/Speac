package org.speac.data_types;

import org.speac.core.types.language.Data;
import org.speac.utilities.FixedList;

public record SpeacList(FixedList<Data<?>> value) implements Data<FixedList<Data<?>>> {
	@Override public FixedList<Data<?>> value() {
		return this.value;
	}

	@Override public boolean matches(Data<?> other) {
		if (!(other instanceof SpeacList))
			return false;
		FixedList<Data<?>> otherContents = ((SpeacList) other).value();
		if (this.value.size() != otherContents.size())
			return false;
		for (int index = 0; index < this.value.size(); index++)
			if (!this.value.get(index).matches(otherContents.get(index)))
				return false;
		return true;
	}

	@Override public String toString(boolean printable) {
		String result = "[";
		for (int index = 0; index < this.value.size(); index++) {
			result += this.value.get(index).toString(false);
			if (index != this.value.size() - 1)
				result += ", ";
		}
		return result + "]";
	}

	@Override public Type type() {
		return Type.LIST;
	}
}
