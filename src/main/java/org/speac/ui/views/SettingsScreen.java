package org.speac.ui.views;

import org.speac.ui.components.CleanButton;
import org.speac.ui.components.CleanComboBox;
import org.speac.ui.internal.*;
import org.speac.utilities.FixedList;
import org.speac.utilities.JsonUtils;
import org.speac.utilities.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsScreen extends JFrame implements ActionListener, Styleable {
	private final ActionListener changeListener = event -> SettingsScreen.this.checkChanges();

	private final JLabel title;
	private final JLabel stylesheetLabel;

	private final CleanComboBox<Stylesheet> stylesheetSelection;
	private final CleanButton okButton;
	private final CleanButton cancelButton;
	private final CleanButton applyButton;

	private final StylesheetLoader loader;
	private final SettingsHandler settings;
	private SettingsHandler.Settings newSettings;

	public SettingsScreen(StylesheetLoader loader, SettingsHandler settings) {
		super("Settings");

		this.loader = loader;
		this.settings = settings;

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new GridBagLayout());
		this.setResizable(false);
		this.setIconImage(Constants.SPEAC_ICON);
		this.getRootPane().setBorder(new EmptyBorder(50, 50, 50, 50));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 0, 5, 0);

		this.title = new JLabel("Settings");
		this.title.setPreferredSize(new Dimension(550, 50));
		this.title.setHorizontalAlignment(SwingConstants.CENTER);
		this.title.setFont(Constants.MAIN_FONT.deriveFont(Font.BOLD, 40.f));
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		this.add(this.title, constraints);

		constraints.insets = new Insets(5, 0, 200, 0); // TODO remove this

		this.stylesheetLabel = new JLabel("Stylesheet:");
		this.stylesheetLabel.setPreferredSize(new Dimension(200, 50));
		this.stylesheetLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.stylesheetLabel.setFont(Constants.MAIN_FONT);
		constraints.gridy = 1;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		this.add(this.stylesheetLabel, constraints);

		FixedList<Stylesheet> availableStylesheets = loader.getAvailableStylesheets();

		this.stylesheetSelection = new CleanComboBox<>(availableStylesheets, settings);
		this.stylesheetSelection.setSelectedIndex(availableStylesheets.indexOf(settings.currentSettings().selectedStylesheet()));
		this.stylesheetSelection.setPreferredSize(new Dimension(350, 50));
		this.stylesheetSelection.addActionListener(this.changeListener);
		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		this.add(this.stylesheetSelection, constraints);

		constraints.insets = new Insets(5, 0, 5, 0); // TODO remove this

		this.okButton = new CleanButton("OK", this, settings);
		this.okButton.setPreferredSize(new Dimension(160, 50));
		constraints.gridy = 2;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		this.add(this.okButton, constraints);

		this.cancelButton = new CleanButton("Cancel", this, settings);
		this.cancelButton.setPreferredSize(new Dimension(160, 50));
		constraints.gridy = 2;
		constraints.gridx = 1;
		constraints.gridwidth = 1;
		this.add(this.cancelButton, constraints);

		this.applyButton = new CleanButton("Apply", this, settings);
		this.applyButton.setPreferredSize(new Dimension(160, 50));
		constraints.gridy = 2;
		constraints.gridx = 2;
		constraints.gridwidth = 1;
		this.add(this.applyButton, constraints);

		this.refreshStylesheet();
		this.checkChanges();

		this.pack();
		UIUtils.center(this);
		this.setVisible(true);
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.getRootPane().setBackground(selectedStylesheet.backgroundColor());
		this.getContentPane().setBackground(selectedStylesheet.backgroundColor());

		this.title.setForeground(selectedStylesheet.foregroundColor());
		this.stylesheetLabel.setForeground(selectedStylesheet.foregroundColor());

		this.stylesheetSelection.refreshStylesheet();
		this.okButton.refreshStylesheet();
		this.cancelButton.refreshStylesheet();
		this.applyButton.refreshStylesheet();
	}

	@Override public void actionPerformed(ActionEvent event) {
		Object sender = event.getSource();
		if (sender == this.okButton) {
			this.applyChanges();
			this.dispose();

		} else if (sender == this.cancelButton) {
			this.dispose();

		} else if (sender == this.applyButton) {
			this.applyChanges();
			this.checkChanges(); // Technically I already know changes have been made
		}
	}

	private void checkChanges() {
		this.newSettings = new SettingsHandler.Settings((Stylesheet) this.stylesheetSelection.getSelectedItem());
		boolean different = !this.settings.compareSettings(this.newSettings);

		this.okButton.setEnabled(different);
		this.applyButton.setEnabled(different);
	}

	private void applyChanges() {
		this.settings.applySettings(this.newSettings);
		try {
			this.settings.dumpToFile();
		} catch (JsonUtils.JsonException ignored) {}

		for (Frame terminal : Frame.getFrames())
			if (terminal instanceof Styleable)
				((Styleable) terminal).refreshStylesheet();
	}
}
