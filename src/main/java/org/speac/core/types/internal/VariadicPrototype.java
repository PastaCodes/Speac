package org.speac.core.types.internal;

import org.speac.core.types.language.Prototype;
import org.speac.utilities.FixedList;
import org.speac.utilities.ListUtils;

public class VariadicPrototype extends Prototype {
	public VariadicPrototype(TokenizedName name) {
		super(name, new FixedList<>());
	}

	// It doesn't make sense to pass another variadic prototype as 'other'
	@Override public boolean matches(Prototype other) {
		if (!other.argumentPositions.isEmpty() && other.argumentPositions.get(0) < 1)
			return false;
		if (!ListUtils.isConsecutive(other.argumentPositions))
			return false;
		return this.nameMatches(other.name);
	}

	private boolean nameMatches(TokenizedName other) {
		if (other.tokens().size() < this.name.tokens().size())
			return false;
		for (int index = 0; index < this.name.tokens().size(); index++)
			if (!this.name.tokens().get(index).equals(other.tokens().get(index)))
				return false;
		return true;
	}

	@Override public String toString() {
		return this.name.toString() + " ...";
	}
}
