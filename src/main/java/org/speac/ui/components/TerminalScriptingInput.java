package org.speac.ui.components;

import org.speac.core.SpeacParser;
import org.speac.core.types.internal.Token;
import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Stylesheet;
import org.speac.ui.views.SpeacTerminal;
import org.speac.utilities.FixedList;

import javax.swing.text.StyledDocument;

public class TerminalScriptingInput extends TerminalInput {
	public TerminalScriptingInput(SpeacTerminal terminal, SettingsHandler settings) {
		super(terminal, settings);
	}

	@Override protected void refreshSyntax(Stylesheet selectedStylesheet) {
		StyledDocument document = this.innerPane.getStyledDocument();
		FixedList<Token> tokens = SpeacParser.parseSyntaxOrError(this.innerPane.getText());
		for (Token token : tokens)
			document.setCharacterAttributes(
					token.beginIndex, token.endIndex - token.beginIndex + 1,
					selectedStylesheet.tokenStyle(token.type).asAttributeSet(),
					true);
	}
}
