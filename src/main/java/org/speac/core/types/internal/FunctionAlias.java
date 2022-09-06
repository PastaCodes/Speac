package org.speac.core.types.internal;

import org.speac.core.types.language.Function;
import org.speac.core.types.language.Prototype;
import org.speac.utilities.FixedList;

/**
 * Defines an identifier for a {@link Function}
 * Adds the arguments correspondence information to the {@link Prototype}
 * This information refers to the language functionality of the Speac language
 * In some languages subjects, verbs, and objects might have to appear in a different order from english
 * For example:
 *		person > likes < food
 * 		cibo > piace a < persona
 * In this case 'cibo' is 'food' in italian, while 'persona' is 'person'
 * The sentence construction is different and therefore there must be a way to store the new order of arguments
 */
public class FunctionAlias {
	public final Prototype name;
	public final FixedList<Integer> argumentsCorrespondence;

	public FunctionAlias(Prototype name, FixedList<Integer> argumentsCorrespondence) {
		this.name = name;
		this.argumentsCorrespondence = argumentsCorrespondence;
	}

	// Used for debugging
	@Override public String toString() {
		return this.name.toString();
	}
}
