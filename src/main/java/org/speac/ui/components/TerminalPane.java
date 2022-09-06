package org.speac.ui.components;

import org.speac.ui.internal.Constants;
import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Styleable;
import org.speac.ui.internal.Stylesheet;
import org.speac.utilities.UIUtils;

import javax.swing.*;
import java.awt.*;

public class TerminalPane implements Styleable {
	private final JScrollPane outerPane;
	private final JTextPane innerPane;

	private final SettingsHandler settings;

	public TerminalPane(SettingsHandler settings) {
		this.settings = settings;

		this.innerPane = new JTextPane();
		this.innerPane.setContentType("text/html");
		this.innerPane.setFont(Constants.MAIN_FONT);
		this.innerPane.setBorder(BorderFactory.createEmptyBorder());
		this.innerPane.setEditable(false);

		this.outerPane = new JScrollPane(this.innerPane);
		this.outerPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.outerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		this.outerPane.getVerticalScrollBar().setUnitIncrement(8); // Affects the speed of the scrollbar

		this.refreshStylesheet();
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.innerPane.setBackground(selectedStylesheet.backgroundColor());

		this.outerPane.setBackground(selectedStylesheet.backgroundColor());
		this.outerPane.setBorder(BorderFactory.createLineBorder(selectedStylesheet.bordersColor()));
		this.outerPane.setCorner(JScrollPane.LOWER_TRAILING_CORNER, UIUtils.createEmptyButton(selectedStylesheet.backgroundColor()));

		JScrollBar horizontalBar = this.outerPane.getHorizontalScrollBar();
		JScrollBar verticalBar = this.outerPane.getVerticalScrollBar();

		horizontalBar.setBackground(selectedStylesheet.backgroundColor());
		verticalBar.setBackground(selectedStylesheet.backgroundColor());

		horizontalBar.setUI(new CleanScrollBarUI(selectedStylesheet));
		verticalBar.setUI(new CleanScrollBarUI(selectedStylesheet));
	}

	public Component getComponent() {
		return this.outerPane;
	}

	// Scroll to the bottommost line
	public void scrollDown() {
		JScrollBar scrollBar = this.outerPane.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	// Text functionality is redirected to the inner pane
	public void setText(String text) {
		this.innerPane.setText(text);
	}

	// Graphical functionality is redirected to the outer pane
	public void setBounds(int x, int y, int width, int height) {
		this.outerPane.setBounds(x, y, width, height);
	}
}
