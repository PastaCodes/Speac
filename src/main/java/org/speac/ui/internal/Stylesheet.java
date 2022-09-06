package org.speac.ui.internal;

import org.speac.core.types.internal.Token;

import java.awt.*;

public interface Stylesheet {
	Color backgroundColor();
	Color bordersColor();
	Color foregroundColor();
	Color notesColor();

	Style errorsStyle();
	Style errorHighlightsStyle();
	Style consoleStyle();
	Style hudStyle();
	Style tokenStyle(Token.Type type);
}
