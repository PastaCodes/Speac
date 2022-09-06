package org.speac.core.types.error;

/**
 * Defines an error whose name, description and solution are constant for all instances
 */
public record SpeacStaticError(String name, String description, String solution) implements SpeacError {
	@Override public String name()			{ return this.name;			}
	@Override public String description()	{ return this.description;	}
	@Override public String solution()		{ return this.solution;		}
}
