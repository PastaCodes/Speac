package org.speac.core.types.language;

import org.speac.core.types.internal.FunctionAlias;
import org.speac.core.types.internal.TokenizedName;
import org.speac.utilities.FixedList;

import java.util.LinkedList;

/**
 * Defines the structure of a function declaration
 * Doesn't provide the relationship between the aliases of a function which are provided by {@link FunctionAlias}'s
 * But it indicates the positions of arguments amongst the function tokens
 * For example:
 * 		repeat from < start > to < end > on < counter name >< code
 * In this case the argumentPositions would be [2, 3, 4, 5]
 * These are all the slots in which there could be arguments:
 * 		0 repeat 1 from 2 to 3 on 4 5
 * Notice that between 4 and 5 there's an empty token ("")
 * In fact the tokenized name for this function would be ["repeat", "from", "to, "on", ""]
 */
public class Prototype {
	public final TokenizedName name;
	public final FixedList<Integer> argumentPositions;

	public Prototype(TokenizedName name, FixedList<Integer> argumentPositions) {
		this.name = name;
		this.argumentPositions = argumentPositions;
	}

	public boolean matches(Prototype other) {
		return this.name.matches(other.name) && this.argumentPositions.equals(other.argumentPositions);
	}

	/**
	 * Transforms the prototype so that it is readable
	 * Example: name.tokens = ["ab", "cd", "ef"], argumentsPositions = [1, 3] --> "ab < > cd ef <"
	 */
	@Override public String toString() {
		LinkedList<String> result = new LinkedList<>(this.name.tokens());

		// Go through every index in reverse to prevent them from shifting
		for (int index = this.name.tokens().size(); index >= 0; index--) {
			String separator;
			if (this.argumentPositions.contains(index))
				if (index == 0)
					separator = "> ";
				else if (index == this.name.tokens().size())
					separator = " <";
				else
					separator = " < > ";
			else
				if (index == 0 || index == this.name.tokens().size())
					separator = "";
				else
					separator = " ";

			result.add(index, separator);
		}

		return String.join("", result).replaceAll(">  <", "><");
	}
}
