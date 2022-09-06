package org.speac.core.types.error;

public interface SpeacError {
	String	NO_TOKEN		= null;
	int		NO_LINE_NUMBER	= -1;
	String	TERMINAL_SOURCE = null;

	String NO_NAME			= null;
	String NO_DESCRIPTION	= null;
	String NO_SOLUTION		= null;

	SpeacError UNKNOWN_ERROR = new SpeacStaticError(
			"Unknown Error",
			SpeacError.NO_DESCRIPTION,
			SpeacError.NO_SOLUTION
	);

	String name();
	String description();
	String solution();
}
