package org.speac.core.types.language;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.utilities.FixedList;

import java.util.LinkedList;

public interface Data<T> {
	T value();

	// default Data<T> duplicate() { return this; }

	boolean matches(Data<?> other);

	String toString(boolean printable);

	Type type();

	/**
	 * This system is only used when checking the type of arguments passed to a function.
	 * All data types are still their own separate classes implementing the #{@link Data} interface,
	 * handling their payloads according to their specific needs.
	 * @see SpeacCoreModuleUtilities#validateArguments(FixedList, Type...)
	 */

	// // // // // // // // // //
	//

	enum Type {
		BOOLEAN {
			@Override public String toString() {
				return "Boolean";
			}
		},
		CODE_BLOCK {
			@Override public String toString() {
				return "Code Block";
			}
		},
		EMPTY {
			@Override public String toString() {
				return "Empty";
			}
		},
		INTEGER {
			@Override public String toString() {
				return "Integer";
			}
		},
		LIST {
			@Override public String toString() {
				return "List";
			}
		},
		REAL {
			@Override public String toString() {
				return "Real";
			}
		},
		STRING {
			@Override public String toString() {
				return "String";
			}
		};

		@Override public abstract String toString();
	}

	Type ANY_TYPE = null;

	static FixedList<Type> extractTypes(FixedList<Data<?>> input) {
		LinkedList<Type> types = new LinkedList<>();
		for (Data<?> item : input)
			types.add(item.type());
		return new FixedList<>(types);
	}

	//
	// // // // // // // // // //
}
