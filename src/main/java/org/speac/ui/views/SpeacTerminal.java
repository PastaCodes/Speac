package org.speac.ui.views;

import org.speac.core.types.internal.SpeacWrapper;
import org.speac.core.types.language.Line;
import org.speac.ui.components.*;
import org.speac.ui.internal.*;
import org.speac.utilities.FixedList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;

public class SpeacTerminal extends JFrame implements SpeacWrapper, ComponentListener, Styleable {
	private final SettingsHandler settings;

	private TerminalPane scriptingPane;
	private TerminalPane monitoringPane;

	private TerminalInput scriptingInput;
	private TerminalInput monitoringInput;

	private TerminalLabel scriptingPaneLabel;
	private TerminalLabel monitoringPaneLabel;
	private TerminalLabel scriptingInputLabel;
	private TerminalLabel monitoringInputLabel;

	private String input;
	private boolean inputReady;

	private int localLineTracker;

	private final ArrayList<String> parsedScriptingLines;
	private final ArrayList<String> parsedMonitoringLines;

	public SpeacTerminal(SettingsHandler settings) {
		super("Speac Terminal");
		this.settings = settings;
		this.initUI();
		this.inputReady = false;
		this.localLineTracker = 1;
		this.parsedScriptingLines = new ArrayList<>();
		this.parsedMonitoringLines = new ArrayList<>();
		this.setVisible(true);
	}

	/**
	 * Initialized the components for the window's UI.
	 * The bounds of each component aren't set because they will be set by {@link SpeacTerminal#componentResized(ComponentEvent)},
	 * which will be called whenever the window is resized but also at the very start.
	 */
	private void initUI() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(null);
		this.setIconImage(Constants.SPEAC_ICON);

		this.position();

		this.scriptingPaneLabel = new TerminalLabel("Instructions Log:", this.settings);
		this.add(this.scriptingPaneLabel);

		this.monitoringPaneLabel = new TerminalLabel("Console Log:", this.settings);
		this.add(this.monitoringPaneLabel);

		this.scriptingPane = new TerminalPane(this.settings);
		this.add(this.scriptingPane.getComponent());

		this.monitoringPane = new TerminalPane(this.settings);
		this.add(this.monitoringPane.getComponent());

		this.scriptingInputLabel = new TerminalLabel("Instructions Input:", this.settings);
		this.add(this.scriptingInputLabel);

		this.monitoringInputLabel = new TerminalLabel("Console Input:", this.settings);
		this.add(this.monitoringInputLabel);

		this.scriptingInput = new TerminalScriptingInput(this, this.settings);
		this.scriptingInput.setEnabled(false);
		this.add(this.scriptingInput.getComponent());

		this.monitoringInput = new TerminalMonitoringInput(this, this.settings);
		this.monitoringInput.setEnabled(false);
		this.add(this.monitoringInput.getComponent());

		this.addComponentListener(this);

		this.refreshStylesheet();
	}

	/**
	 * Adjusts the window's position and size.
	 * It will have a pre-established aspect ratio and will fill a certain percentage of the most limiting screen axis.
	 *
	 * For example if the window has a more portrait aspect ratio than the screen
	 * the height of the window will be a certain percentage of the screen height
	 * and its width will be derived from the height and aspect ratio.
	 *
	 * On the other hand if the window has a more landscape aspect ratio than the screen
	 * the width of the window will be a certain percentage of the screen width
	 * and its height will be derived from the width and aspect ratio.
	 *
	 * 'Certain percentage' refers to {@link Constants#FILLING_INDEX}.
	 */
	private void position() {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle frameBounds = new Rectangle();
		if ((double) screenDimension.width / screenDimension.height > Constants.DEFAULT_ASPECT_RATIO) {
			frameBounds.height = (int) (Constants.FILLING_INDEX * screenDimension.height);
			frameBounds.width = (int) (Constants.DEFAULT_ASPECT_RATIO * frameBounds.height);
		} else {
			frameBounds.width = (int) (Constants.FILLING_INDEX * screenDimension.width);
			frameBounds.height = (int) (frameBounds.width / Constants.DEFAULT_ASPECT_RATIO);
		}
		frameBounds.x = (int) ((screenDimension.width - frameBounds.width) / 2.);
		frameBounds.y = (int) ((screenDimension.height - frameBounds.height) / 2.);
		this.setBounds(frameBounds);
	}

	@Override public void componentResized(ComponentEvent event) {
		Dimension size = this.getContentPane().getSize();
		int hw = size.width / 2; // half of the window width
		int h = size.height;
		int lh = Constants.LABELS_HEIGHT;
		int ih = Constants.INPUTS_HEIGHT;
		this.scriptingPaneLabel		.setBounds(0,	0,				hw, lh			    );
		this.monitoringPaneLabel	.setBounds(hw,	0,				hw, lh			    );
		this.scriptingPane			.setBounds(0,	lh,				hw, h - ih - 2 * lh	);
		this.monitoringPane			.setBounds(hw,	lh,				hw, h - ih - 2 * lh	);
		this.scriptingInputLabel	.setBounds(0,	h - ih - lh,	hw, lh			    );
		this.monitoringInputLabel	.setBounds(hw,	h - ih - lh,	hw, lh			    );
		this.scriptingInput			.setBounds(0,	h - ih,		    hw, ih			    );
		this.monitoringInput		.setBounds(hw,	h - ih,		    hw, ih			    );
	}

	@Override public void componentMoved(ComponentEvent event) {}
	@Override public void componentShown(ComponentEvent event) {}
	@Override public void componentHidden(ComponentEvent event) {}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.getContentPane().setBackground(selectedStylesheet.backgroundColor());

		this.scriptingPane.refreshStylesheet();
		this.monitoringPane.refreshStylesheet();

		this.scriptingInput.refreshStylesheet();
		this.monitoringInput.refreshStylesheet();

		this.scriptingPaneLabel.refreshStylesheet();
		this.monitoringPaneLabel.refreshStylesheet();
		this.scriptingInputLabel.refreshStylesheet();
		this.monitoringInputLabel.refreshStylesheet();

		this.updateAllLines();
	}

	/**
	 * Replaces the instructions log with the contents of a script file from path {@param path}.
	 */
	public void displayScript(Path path) {
		this.parsedScriptingLines.clear();
		this.parsedMonitoringLines.clear();

		this.setTitle("Speac Terminal - " + path);

		FixedList<Line> lines = Line.readLinesFromScript(path, true);

		assert lines != null; // Should never fail because the path is derived from a dropped file (must exist)
		FixedList<String> linesContents = new FixedList<>(lines.stream().map(Line::contents).toList());
		this.parsedScriptingLines.addAll(HtmlHighlighter.highlightScript(linesContents));

		this.updateAllLines();
	}

	private void updateLinesU(ArrayList<String> lines, TerminalPane output) {
		if (!this.isVisible()) return; // Allows the ui thread to finish after closing the window
		this.updateLines(new FixedList<>(lines), output);
	}

	private void updateLines(FixedList<String> lines, TerminalPane output) {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		String HTML = HtmlHighlighter.joinLines(lines);
		HTML = HtmlHighlighter.applyStylesheet(HTML, selectedStylesheet);

		output.setText("<html><head><style>span { font-family:\"Consolas\"; font-size: 20; }</style></head><body><pre>" + HTML + "</pre></body></html>");
		SwingUtilities.invokeLater(output::scrollDown);
	}

	private void updateAllLines() {
		this.updateLinesU(this.parsedScriptingLines, this.scriptingPane);
		this.updateLinesU(this.parsedMonitoringLines, this.monitoringPane);
	}

	public synchronized void supplySignal() {
		if (this.scriptingInput.isEnabled())
			this.input = this.scriptingInput.getText();
		else /* if (this.monitoringInput.isEnabled()) */
			this.input = this.monitoringInput.getText();
		this.inputReady = true;
		this.notifyAll();
	}

	@Override public void consoleOut(String line) {
		this.parsedMonitoringLines.add(HtmlHighlighter.highlightConsoleOut(line));
		this.updateLinesU(this.parsedMonitoringLines, this.monitoringPane);
	}

	@Override public synchronized String consoleIn() {
		this.monitoringInput.setEnabled(true);
		SwingUtilities.invokeLater(() -> this.monitoringInput.requestFocus());
		while (!this.inputReady) {
			try { this.wait(); } catch (InterruptedException ignored) {}
		}
		this.inputReady = false;
		this.monitoringInput.setText("");

		try {
			SwingUtilities.invokeAndWait(() -> this.monitoringInput.setEnabled(false));
		} catch (InvocationTargetException | InterruptedException ignored) {}

		this.parsedMonitoringLines.add(HtmlHighlighter.highlightConsoleIn(this.input));

		this.updateLinesU(this.parsedMonitoringLines, this.monitoringPane);
		return this.input;
	}

	@Override public void errorOut(
			String name,        String description,
			String solution,    int lineNumber,
			String source,      String incriminatedToken) {
		this.parsedScriptingLines.addAll(HtmlHighlighter.highlightError(name, description, solution, lineNumber, source, incriminatedToken));
		this.updateLinesU(this.parsedScriptingLines, this.scriptingPane);
	}

	@Override public synchronized String instructionIn() {
		this.scriptingInput.setEnabled(true);
		SwingUtilities.invokeLater(() -> this.scriptingInput.requestFocus());
		while (!this.inputReady) {
			try {
				this.wait();
			} catch (InterruptedException ignored) {}
		}
		this.inputReady = false;
		this.scriptingInput.setText("");

		try {
			SwingUtilities.invokeAndWait(() -> this.scriptingInput.setEnabled(false));
		} catch (InvocationTargetException | InterruptedException ignored) {}

		this.parsedScriptingLines.add(HtmlHighlighter.highlightInstructionIn(this.input, this.localLineTracker++));

		this.updateLinesU(this.parsedScriptingLines, this.scriptingPane);
		return this.input;
	}

	@Override public void pause() {}
}
