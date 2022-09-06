package org.speac.ui.components;

import org.speac.ui.internal.Constants;
import org.speac.ui.internal.SettingsHandler;
import org.speac.ui.internal.Styleable;
import org.speac.ui.internal.Stylesheet;
import org.speac.utilities.FixedList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CleanComboBox<E> extends JComboBoxFix<E> implements Styleable {
	private static final Border PADDING_BORDER = new EmptyBorder(12, 10, 5, 10);

	private final SettingsHandler settings;

	public CleanComboBox(FixedList<E> items, SettingsHandler settings) {
		super(items);

		this.settings = settings;

		this.setFont(Constants.MAIN_FONT);
		this.setMaximumRowCount(2); // TODO find good value

		this.setArrowButtonCreation(() -> new JButton("<html>&#11167;</html>"));
		this.setCellBorder(CleanComboBox.PADDING_BORDER);

		this.refreshStylesheet();
	}

	@Override public void refreshStylesheet() {
		Stylesheet selectedStylesheet = this.settings.currentSettings().selectedStylesheet();

		this.setCurrentValueBackground(selectedStylesheet.backgroundColor());
		this.setCurrentValueBorder(new LineBorder(selectedStylesheet.bordersColor()));

		this.setCellForegroundColor(selectedStylesheet.foregroundColor());
		this.setCellBackgroundColor(selectedStylesheet.backgroundColor());
		this.setSelectedCellBackgroundColor(selectedStylesheet.bordersColor());

		this.update();

		this.getPopup().setBorder(new LineBorder(selectedStylesheet.bordersColor()));

		JButton arrowButton = this.getArrowButton();
		arrowButton.setForeground(selectedStylesheet.foregroundColor());
		arrowButton.setBackground(selectedStylesheet.bordersColor());

		JScrollBar scrollBar = this.getScrollBar();
		scrollBar.setUI(new CleanScrollBarUI(selectedStylesheet));
	}
}
