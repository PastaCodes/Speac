package org.speac.ui.components;

import org.speac.ui.internal.Constants;
import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Styleable;
import org.speac.ui.internal.Stylesheet;
import org.speac.ui.views.SpeacTerminal;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class TerminalInput implements KeyListener, Styleable {
	private static final Border DISABLED_BORDER = new EmptyBorder((Constants.INPUTS_HEIGHT - Constants.FONT_HEIGHT - 10) / 2, 12, 0, 12);
	private Border enabledBorder;

	private final SpeacTerminal terminal;
	protected final JTextPane innerPane;
	protected final JScrollPane outerPane;

	private final SettingsHandler settings;

	public TerminalInput(SpeacTerminal terminal, SettingsHandler settings) {
		super();

		this.terminal = terminal;
		this.settings = settings;

		this.innerPane = new JTextPane();
		this.innerPane.addKeyListener(this);
		this.innerPane.addCaretListener(event -> this.scrollRight());
		this.innerPane.setFont(Constants.MAIN_FONT);

		JPanel noWrapPane = new JPanel(new BorderLayout());
		noWrapPane.add(this.innerPane);

		this.outerPane = new JScrollPane(noWrapPane);

		this.outerPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.outerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		this.refreshStylesheet();
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.innerPane.setBackground(selectedStylesheet.backgroundColor());
		this.innerPane.setCaretColor(selectedStylesheet.foregroundColor());

		this.outerPane.setBackground(selectedStylesheet.backgroundColor());

		this.enabledBorder = new CompoundBorder(BorderFactory.createLineBorder(selectedStylesheet.bordersColor()), TerminalInput.DISABLED_BORDER);

		if (this.isEnabled())
			this.outerPane.setBorder(this.enabledBorder);

		this.refreshSyntax(selectedStylesheet);
	}

	@Override public void keyPressed(KeyEvent event) {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();
		if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			event.consume();
			this.terminal.supplySignal();
		} else
			SwingUtilities.invokeLater(() -> this.refreshSyntax(selectedStylesheet));
	}
	@Override public void keyReleased(KeyEvent event) {}
	@Override public void keyTyped(KeyEvent event) {}

	public Component getComponent() {
		return this.outerPane;
	}

	/**
	 * Similar to {@link TerminalPane#scrollDown()}
	 */
	private void scrollRight() {
		if (this.innerPane.getCaretPosition() == this.innerPane.getDocument().getLength()) {
			JScrollBar scrollBar = this.outerPane.getHorizontalScrollBar();
			scrollBar.setValue(scrollBar.getMaximum());
		}
	}

	protected abstract void refreshSyntax(Stylesheet selectedStylesheet);

	public boolean isEnabled() {
		return this.innerPane.isEnabled();
	}
	public void setEnabled(boolean enabled) {
		if (enabled)
			this.outerPane.setBorder(this.enabledBorder);
		else
			this.outerPane.setBorder(TerminalInput.DISABLED_BORDER);
		this.innerPane.setEnabled(enabled);
	}

	public String getText() {
		return this.innerPane.getText();
	}
	public void setText(String text) {
		this.innerPane.setText(text);
	}

	public void requestFocus() {
		this.innerPane.requestFocus();
	}

	public void setBounds(int x, int y, int width, int height) {
		this.outerPane.setBounds(x, y, width, height);
	}
}
