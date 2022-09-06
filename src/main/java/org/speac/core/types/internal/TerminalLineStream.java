package org.speac.core.types.internal;

import org.speac.core.types.language.Line;

public class TerminalLineStream implements LineStream {
	private final SpeacWrapper wrapper;
	private int lineTracker;

	public TerminalLineStream(SpeacWrapper wrapper) {
		this.wrapper = wrapper;
		this.lineTracker = 1;
	}

	@Override
	public Line supply() {
		return new Line(this.wrapper.instructionIn(), this.lineTracker++);
	}
}
