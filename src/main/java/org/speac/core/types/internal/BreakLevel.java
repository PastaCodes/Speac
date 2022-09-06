package org.speac.core.types.internal;

import org.speac.utilities.Reference;

/**
 * Simplifies slightly the implementation of the break level
 */
public class BreakLevel {
	public static final int RETURNED = -1;
	private static final int BASE_VALUE = 0;

	private static class Inner extends Reference<Integer> {
		public void raise(int amount) {
			this.value += amount;
		}

		public void consume() {
			this.value--;
		}
	}

	private final Inner inner;

	public BreakLevel() {
		this.inner = new Inner();
		this.reset();
	}

	public int get() {
		return this.inner.get();
	}

	public void raise(int amount) {
		if (amount <= 0)
			throw new IllegalArgumentException("amount must be strictly positive");
		this.inner.raise(amount);
	}

	public void consume() {
		this.inner.consume();
	}

	public void setReturned() {
		this.inner.set(BreakLevel.RETURNED);
	}

	public void reset() {
		this.inner.set(BreakLevel.BASE_VALUE);
	}

	// Used for debugging
	@Override public String toString() {
		return this.inner.get().toString();
	}
}
