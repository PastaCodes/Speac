package org.speac.utilities;

import java.util.LinkedList;

public final class ListUtils {
	@SafeVarargs public static <T> void appendToFixedListReference(Reference<FixedList<T>> destination, T ... items) {
		LinkedList<T> result = new LinkedList<>();
		result.addAll(destination.get());
		result.addAll(new FixedList<>(items));
		destination.set(new FixedList<>(result));
	}

	public static FixedList<Integer> generateConsecutive(int length, int start) {
		Integer[] result = new Integer[length];
		for (int index = 0; index < length; index++)
			result[index] = start + index;
		return new FixedList<>(result);
	}

	public static boolean isConsecutive(FixedList<Integer> list) {
		for (int index = 0; index < list.size() - 1; index++)
			if (list.get(index + 1) != list.get(index) + 1)
				return false;
		return true;
	}
}