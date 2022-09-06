package org.speac.core.types.language;

import org.speac.core.types.internal.TokenizedName;

import java.util.ArrayList;

/**
 * A variable has a list of names that identify it and some contents
 */
public class Variable {
	public ArrayList<TokenizedName> names;
	public Data<?> contents;

	public Variable(Data<?> value) {
		this.names = new ArrayList<>();
		this.contents = value;
	}
	public Variable(TokenizedName mainName, Data<?> value) {
		this(value);
		this.names.add(mainName);
	}

	/**
	 * @return if the name was added (true) or if it was already present (false)
	 */
	public boolean addName(TokenizedName newName) {
		if (this.hasName(newName))
			return false;
		this.names.add(newName);
		return true;
	}

	public boolean hasName(TokenizedName check) {
		return this.names.stream()
				.anyMatch(name -> name.matches(check));
		/*
		for (TokenizedName name : this.names)
			if (name.matches(check))
				return true;
		return false;
		*/
	}

	public void mergeNames(Variable other) {
		other.names.forEach(this::addName);
		/*
		for (TokenizedName otherName : other.names)
			if (!this.hasName(otherName))
				this.addName(otherName);
		*/
	}

	// Used for debugging
	@Override public String toString() {
		return this.names.toString();
	}
}
