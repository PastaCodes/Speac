package org.speac.utilities;

// Functions much like a c-style pointer
public class Reference<T> {
	protected T value;
	protected boolean set;

	public Reference(T value) {
		this.value = value;
		this.set = true;
	}
	public Reference() {
		this.set = false;
	}

	public boolean isSet() {
		return this.set;
	}
	public T get() {
		if (!this.set)
			throw new NullPointerException("reference's value was accessed before initialization");
		return this.value;
	}
	public void set(T value) {
		this.value = value;
		this.set = true;
	}
}
