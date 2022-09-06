package org.speac.utilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

public class FixedList<E> extends FixedLengthList<E> {
	protected class FixedIterator extends FixedLengthIterator {
		public FixedIterator(int initialCursorPosition) {
			super(initialCursorPosition);
		}
		public FixedIterator() {
			super();
		}

		// Unsupported because of immutability
		@Override public void set(E item) {
			throw new UnsupportedOperationException();
		}
	}

	@SafeVarargs public FixedList(E ... items) {
		super(items);
	}
	public FixedList(Collection<E> items) {
		super(items);
	}

	@Override public Iterator<E> iterator() {
		return new FixedIterator();
	}

	@Override public ListIterator<E> listIterator() {
		return new FixedIterator();
	}

	@Override public ListIterator<E> listIterator(int index) {
		return new FixedIterator(index);
	}

	// Unsupported because of immutability
	@Override public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
}
