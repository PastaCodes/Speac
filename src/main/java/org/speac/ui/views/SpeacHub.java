package org.speac.ui.views;

import org.speac.core.SpeacRunner;
import org.speac.ui.components.CleanButton;
import org.speac.ui.internal.*;
import org.speac.utilities.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The Speac Hub is the first thing that shows up when the Speac jar is executed.
 */
public class SpeacHub extends JFrame implements ActionListener, Styleable {
	private final JLabel title;
	private final JLabel scriptButtonTagLabel;
	private final JLabel scriptButtonTipLabel;

	private final CleanButton scriptButton;
	private final CleanButton terminalButton;
	private final CleanButton settingsButton;
	private final CleanButton quitAllButton;
	private final CleanButton quitButton;

	private final StylesheetLoader loader;
	private final SettingsHandler settings;

	public SpeacHub(StylesheetLoader loader, SettingsHandler settings) {
		super("Speac Hub");

		this.loader = loader;
		this.settings = settings;

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // quit() method is called by a window listener
		this.setLayout(new GridBagLayout());
		this.setResizable(false);
		this.setIconImage(Constants.SPEAC_ICON);
		this.getRootPane().setBorder(new EmptyBorder(50, 50, 50, 50));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 0, 5, 0);

		this.title = new JLabel("Speac Hub");
		this.title.setPreferredSize(new Dimension(350, 50));
		this.title.setHorizontalAlignment(SwingConstants.CENTER);
		this.title.setFont(Constants.MAIN_FONT.deriveFont(Font.BOLD, 40.f));
		constraints.gridy = 0;
		this.add(this.title, constraints);

		this.scriptButton = new CleanButton("", this, this.settings);
		this.scriptButton.setPreferredSize(new Dimension(350, 100));
		this.scriptButton.setEnabled(false);
		this.scriptButton.setBorder(BorderFactory.createEmptyBorder());
		this.scriptButton.setLayout(new GridBagLayout());

		this.scriptButtonTagLabel = new JLabel("Run Script(s)");
		this.scriptButtonTagLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.scriptButtonTagLabel.setFont(Constants.MAIN_FONT);
		constraints.gridy = 0;
		this.scriptButton.add(this.scriptButtonTagLabel, constraints);

		this.scriptButtonTipLabel = new JLabel("Drag file(s) here");
		this.scriptButtonTipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.scriptButtonTipLabel.setFont(Constants.MAIN_FONT);
		constraints.gridy = 1;
		this.scriptButton.add(this.scriptButtonTipLabel, constraints);

		constraints.gridy = 1;
		this.add(this.scriptButton, constraints);

		this.scriptButton.setDropTarget(new DropTarget() {
			@Override public synchronized void drop(DropTargetDropEvent event) {
				event.acceptDrop(DnDConstants.ACTION_MOVE);
				try {
					@SuppressWarnings("unchecked") List<File> droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						Thread scriptThread = new Thread(() -> {
							SpeacTerminal terminal = new SpeacTerminal(SpeacHub.this.settings);
							terminal.displayScript(file.toPath());
							new SpeacRunner(terminal).runScript(file.toPath());
						});
						scriptThread.setDaemon(true);
						scriptThread.start();
					}
				} catch (UnsupportedFlavorException | IOException ignored) {}
			}
		});

		this.terminalButton = new CleanButton("Open New Terminal", this, this.settings);
		constraints.gridy = 2;
		this.add(this.terminalButton, constraints);

		this.quitAllButton = new CleanButton("Close All Terminals", this, this.settings);
		constraints.gridy = 3;
		this.add(this.quitAllButton, constraints);

		this.settingsButton = new CleanButton("Change Settings", this, this.settings);
		constraints.gridy = 4;
		this.add(this.settingsButton, constraints);

		this.quitButton = new CleanButton("Exit Hub", this, this.settings);
		constraints.gridy = 5;
		this.add(this.quitButton, constraints);

		this.refreshStylesheet();

		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				SpeacHub.this.quit();
			}
		});

		this.pack();
		UIUtils.center(this);
		this.setVisible(true);
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.getRootPane().setBackground(selectedStylesheet.backgroundColor());
		this.getContentPane().setBackground(selectedStylesheet.backgroundColor());

		this.title.setForeground(selectedStylesheet.foregroundColor());
		this.scriptButtonTagLabel.setForeground(selectedStylesheet.foregroundColor());
		this.scriptButtonTipLabel.setForeground(selectedStylesheet.notesColor());

		this.scriptButton.refreshStylesheet();
		this.terminalButton.refreshStylesheet();
		this.quitAllButton.refreshStylesheet();
		this.settingsButton.refreshStylesheet();
		this.quitButton.refreshStylesheet();
	}

	@Override public void actionPerformed(ActionEvent event) {
		Object sender = event.getSource();
		if (sender == this.terminalButton) {
			Thread terminalThread = new Thread(
					() -> new SpeacRunner(new SpeacTerminal(this.settings)).runTerminal()
			);
			terminalThread.setDaemon(true);
			terminalThread.start();

		} else if (sender == this.settingsButton) {
			// Check if the settings screen is already open
			boolean alreadyOpen = false;
			for (Frame terminal : Frame.getFrames())
				if (terminal instanceof SettingsScreen && terminal.isVisible()) {
					terminal.requestFocus();
					alreadyOpen = true;
					break;
				}
			if (!alreadyOpen)
				new SettingsScreen(this.loader, this.settings);

		} else if (sender == this.quitButton) {
			this.quit();

		} else if (sender == this.quitAllButton) {
			for (Frame terminal : Frame.getFrames())
				if (terminal instanceof SpeacTerminal && terminal.isVisible())
					terminal.dispose();
		}
	}

	private void quit() {
		// Close the settings screen if it's open
		for (Frame terminal : Frame.getFrames())
			if (terminal instanceof SettingsScreen && terminal.isVisible()) {
				terminal.dispose();
				break; // I assume at most one is visible at any given moment
			}
		// Then quit
		this.dispose();
	}
}