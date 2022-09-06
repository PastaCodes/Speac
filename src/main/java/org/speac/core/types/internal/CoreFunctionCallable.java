package org.speac.core.types.internal;

import org.speac.core.SpeacRunner;
import org.speac.core.types.error.SpeacRaisedError;
import org.speac.core.types.language.CoreFunction;
import org.speac.core.types.language.Data;
import org.speac.utilities.FixedList;
import org.speac.utilities.Reference;

/**
 * Defines the java implementation of a {@link CoreFunction}
 */
public interface CoreFunctionCallable {
	SpeacRaisedError call(
			SpeacRunner runner,
			FixedList<Data<?>> arguments,
			Scope scope,
			Scope parentScope,
			BreakLevel breakLevel,
			Reference<Data<?>> returnValue
	);
}
