package org.speac.core.types.internal;

import org.speac.core.types.language.Line;
import org.speac.utilities.FixedList;

public class CodeBlockLineStream implements LineStream {
	private final FixedList<Line> codeBlock;
	private int readOffset;

	public CodeBlockLineStream(FixedList<Line> block) {
		this.codeBlock = block;
		this.readOffset = 0;
	}

	@Override public Line supply() {
		if (this.readOffset == this.codeBlock.size())
			return null;
		return this.codeBlock.get(this.readOffset++);
	}
}
