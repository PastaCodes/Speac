package org.speac.core.types.language;

import org.speac.core.types.internal.FunctionAlias;
import org.speac.core.types.internal.VariadicAlias;
import org.speac.core.types.internal.VariadicPrototype;
import org.speac.utilities.FixedList;
import org.speac.utilities.ListUtils;
import org.speac.utilities.Reference;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Contains all properties that are common to {@link CoreFunction}'s and {@link SpeacFunction}'s,
 * makes it easier to look for functions amongst both types
 * A function also has a list of aliases that identify it
 */
public abstract class Function {
	public ArrayList<FunctionAlias> aliases;

	public Function() {
		this.aliases = new ArrayList<>();
	}
	public Function(Prototype prototype) {
		this();
		if (prototype instanceof VariadicPrototype)
			this.aliases.add(new FunctionAlias(prototype, VariadicAlias.VARIADIC_CORRESPONDENCE));
		else
			this.aliases.add(new FunctionAlias(
					prototype,
					ListUtils.generateConsecutive(prototype.argumentPositions.size(), 0)
			));
	}

	/**
	 * @return if the alias was added (true) or if it was already present (false)
	 */
	public boolean addAlias(FunctionAlias newAlias) {
		if (this.hasAlias(newAlias.name, new Reference<>()))
			return false;
		this.aliases.add(newAlias);
		return true;
	}

	/**
	 * @param argumentsCorrespondence is modified only if the prototype matches, can be null if you don't need this feedback
	 */
	public boolean hasAlias(Prototype check, Reference<FixedList<Integer>> argumentsCorrespondence) {
		Optional<FunctionAlias> match = this.aliases.stream()
				.filter(alias -> alias.name.matches(check))
				.findFirst();
		if (match.isPresent()) {
			if (argumentsCorrespondence != null)
				argumentsCorrespondence.set(match.get().argumentsCorrespondence);
			return true;
		}
		return false;
		/*
		for (FunctionAlias alias : this.aliases)
			if (alias.name.matches(check)) {
				if (argumentsCorrespondence != null)
					argumentsCorrespondence.set(alias.argumentsCorrespondence);
				return true;
			}
		return false;
		*/
	}

	public void mergeAliases(Function other) {
		other.aliases.forEach(this::addAlias);
	}

	// Used for debugging
	@Override public String toString() {
		return this.aliases.toString();
	}
}
