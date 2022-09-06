package org.speac.ui.components;

import org.speac.ui.internal.Constants;
import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Styleable;
import org.speac.ui.internal.Stylesheet;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class TerminalLabel extends JLabel implements Styleable {
	private static final Border SPACER = new EmptyBorder(8, 15, 0, 0);

	private final SettingsHandler settings;

	public TerminalLabel(String text, SettingsHandler settings) {
		super(text);

		this.settings = settings;

		this.setFont(Constants.MAIN_FONT);
		this.setBorder(TerminalLabel.SPACER);

		this.refreshStylesheet();
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.setForeground(selectedStylesheet.foregroundColor());
	}
}
