package org.speac.ui.components;

import org.speac.ui.internal.Constants;
import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Styleable;
import org.speac.ui.internal.Stylesheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CleanButton extends JButton implements Styleable {
	private static final Dimension SIZE = new Dimension(350, 75);

	private final SettingsHandler settings;

	public CleanButton(String text, ActionListener listener, SettingsHandler settings) {
		super(text);

		this.settings = settings;

		this.setPreferredSize(CleanButton.SIZE);
		this.setFont(Constants.MAIN_FONT);
		this.setFocusPainted(false);
		this.addActionListener(listener);

		this.refreshStylesheet();
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.setForeground(selectedStylesheet.foregroundColor());
		this.setBackground(selectedStylesheet.bordersColor());
	}
}
