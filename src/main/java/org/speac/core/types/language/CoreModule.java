package org.speac.core.types.language;

import org.speac.core.types.internal.Scope;

// A way to standardize those external jar files containing CoreFunctions
public interface CoreModule {
	void load(final Scope scope);
}
