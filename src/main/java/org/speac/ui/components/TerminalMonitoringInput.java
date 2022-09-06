package org.speac.ui.components;

import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Stylesheet;
import org.speac.ui.views.SpeacTerminal;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TerminalMonitoringInput extends TerminalInput {
	public TerminalMonitoringInput(SpeacTerminal terminal, SettingsHandler settings) {
		super(terminal, settings);
	}

	@Override protected void refreshSyntax(Stylesheet selectedStylesheet) {
		StyledDocument document = this.innerPane.getStyledDocument();
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, selectedStylesheet.foregroundColor());
		document.setCharacterAttributes(0, document.getLength(), attributes, true);
	}
}
