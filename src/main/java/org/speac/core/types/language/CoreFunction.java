package org.speac.core.types.language;

import org.speac.core.types.internal.CoreFunctionCallable;

/**
 * Refers to a function implemented in java that interacts directly with the Speac core
 * but can be called in the Speac environment just like a SpeacFunction.
 * @see Function
 * @see SpeacFunction
 */
public class CoreFunction extends Function {
	public final CoreFunctionCallable callable;

	public CoreFunction(CoreFunctionCallable callable) {
		super();
		this.callable = callable;
	}
	public CoreFunction(Prototype prototype, CoreFunctionCallable callable) {
		super(prototype);
		this.callable = callable;
	}
}
