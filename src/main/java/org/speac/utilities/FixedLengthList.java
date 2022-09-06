package org.speac.utilities;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class FixedLengthList<E> implements List<E> {
	public static final int NOT_FOUND = -1;

	protected class FixedLengthIterator implements ListIterator<E> {
		protected int cursor;

		public FixedLengthIterator(int initialCursorPosition) {
			this.cursor = initialCursorPosition;
		}
		public FixedLengthIterator() {
			this.cursor = 0;
		}

		@Override public boolean hasNext() {
			return this.cursor < FixedLengthList.this.items.length;
		}

		@Override public E next() {
			try {
				return FixedLengthList.this.items[this.cursor++];
			} catch (IndexOutOfBoundsException exception) {
				throw new NoSuchElementException();
			}
		}

		@Override public boolean hasPrevious() {
			return this.cursor > 0;
		}

		@Override public E previous() {
			try {
				return FixedLengthList.this.items[--this.cursor];
			} catch (IndexOutOfBoundsException exception) {
				throw new NoSuchElementException();
			}
		}

		@Override public int nextIndex() {
			return this.cursor + 1;
		}

		@Override public int previousIndex() {
			return this.cursor - 1;
		}

		@Override public void set(E item) {
			FixedLengthList.this.items[this.cursor] = item;
		}

		// Unsupported because of fixed length

		@Override public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override public void add(E item) {
			throw new UnsupportedOperationException();
		}
	}

	protected final E[] items;

	@SafeVarargs public FixedLengthList(E ... items) {
		this.items = items.clone();
	}
	@SuppressWarnings("unchecked") public FixedLengthList(Collection<E> items) {
		this.items = (E[]) items.toArray();
	}

	// Useful methods

	public E getLast() {
		if (this.items.length < 1)
			throw new NoSuchElementException();
		return this.items[this.items.length - 1];
	}

	@FunctionalInterface public interface ConsumerReturn<T, R> {
		Optional<R> accept(T t);
	}

	@FunctionalInterface public interface BiConsumerReturn<V, W, R> {
		Optional<R> accept(V v, W w);
	}

	public <R> Optional<R> forEachReturn(ConsumerReturn<E, R> action) {
		for (E item : this.items) {
			Optional<R> returned = action.accept(item);
			if (returned.isPresent())
				return returned;
		}
		return Optional.empty();
	}

	public <R> R forEachReturn(ConsumerReturn<E, R> action, R defaultReturnValue) {
		Optional<R> returned = this.forEachReturn(action);
		return returned.orElse(defaultReturnValue);
	}

	public void enumerate(BiConsumer<Integer, E> action) {
		IntStream.range(0, this.items.length)
				.forEach(
						index -> action.accept(index, this.items[index])
				);
	}

	public <R> Optional<R> enumerateReturn(BiConsumerReturn<Integer, E, R> action) {
		for (int index = 0; index < this.items.length; index++) {
			Optional<R> returned = action.accept(index, this.items[index]);
			if (returned.isPresent())
				return returned;
		}
		return Optional.empty();
	}

	public <R> R enumerateReturn(BiConsumerReturn<Integer, E, R> action, R defaultReturnValue) {
		Optional<R> returned = this.enumerateReturn(action);
		return returned.orElse(defaultReturnValue);
	}

	public static <V, W> void zip(FixedLengthList<V> first, FixedLengthList<W> second, BiConsumer<V, W> action) {
		if (first.items.length != second.items.length)
			throw new UnsupportedOperationException("lists have different lengths");
		int length = first.items.length & second.items.length;
		IntStream.range(0, length)
				.forEach(
						index -> action.accept(first.get(index), second.get(index))
				);
	}

	public static <V, W, R> Optional<R> zipReturn(FixedLengthList<V> first, FixedLengthList<W> second, BiConsumerReturn<V, W, R> action) {
		if (first.items.length != second.items.length)
			throw new UnsupportedOperationException("lists have different lengths");
		int length = first.items.length & second.items.length;
		for (int index = 0; index < length; index++) {
			Optional<R> returned = action.accept(first.get(index), second.get(index));
			if (returned.isPresent())
				return returned;
		}
		return Optional.empty();
	}

	public static <V, W, R> R zipReturn(FixedLengthList<V> first, FixedLengthList<W> second, BiConsumerReturn<V, W, R> action, R defaultReturnValue) {
		Optional<R> returned = FixedLengthList.zipReturn(first, second, action);
		return returned.orElse(defaultReturnValue);
	}

	// Standard list methods

	@Override public int size() {
		return this.items.length;
	}

	@Override public boolean isEmpty() {
		return this.items.length == 0;
	}

	@Override public boolean contains(Object check) {
		return this.indexOf(check) != FixedLengthList.NOT_FOUND;
	}

	@Override public E[] toArray() {
		return this.items.clone();
	}

	@Override public boolean containsAll(Collection<?> check) {
		for (Object item : check)
			if (!this.contains(item))
				return false;
		return true;
	}

	@Override public E get(int index) {
		return this.items[index];
	}

	@Override public int indexOf(Object check) {
		for (int index = 0; index < this.items.length; index++)
			if (Objects.equals(this.items[index], check))
				return index;
		return FixedLengthList.NOT_FOUND;
	}

	@Override public int lastIndexOf(Object check) {
		for (int index = this.items.length - 1; index >= 0; index--)
			if (Objects.equals(this.items[index], check))
				return index;
		return FixedLengthList.NOT_FOUND;
	}

	@Override public FixedList<E> subList(int fromIndex, int toIndex) {
		return new FixedList<>(Arrays.copyOfRange(this.items, fromIndex, toIndex));
	}

	@Override public Iterator<E> iterator() {
		return new FixedLengthIterator();
	}

	@Override public ListIterator<E> listIterator() {
		return new FixedLengthIterator();
	}

	@Override public ListIterator<E> listIterator(int index) {
		return new FixedLengthIterator(index);
	}

	@Override public E set(int index, E item) {
		E oldItem = this.items[index];
		this.items[index] = item;
		return oldItem;
	}

	// Object methods

	@Override public String toString() {
		final boolean isOfStrings = this.items.length > 0 && this.items[0] instanceof String;
		String result = "[";
		for (int index = 0; index < this.items.length; index++) {
			if (isOfStrings)
				result += "\"" + this.items[index] + "\"";
			else
				result += this.items[index];
			if (index != this.items.length - 1)
				result += ", ";
		}
		return result + "]";
	}

	@Override public boolean equals(Object other) {
		if (other instanceof FixedLengthList<?> otherList) {
			for (int index = 0; index < this.items.length; index++)
				if (!this.items[index].equals(otherList.items[index]))
					return false;
			return true;
		}
		return false;
	}

	// Unsupported because of fixed length

	@Override public <T> T[] toArray(T[] destination) {
		throw new UnsupportedOperationException();
	}

	@Override public boolean add(E item) {
		throw new UnsupportedOperationException();
	}

	@Override public boolean remove(Object item) {
		throw new UnsupportedOperationException();
	}

	@Override public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override public void add(int index, E item) {
		throw new UnsupportedOperationException();
	}

	@Override public E remove(int index) {
		throw new UnsupportedOperationException();
	}
}
