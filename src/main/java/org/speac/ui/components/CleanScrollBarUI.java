package org.speac.ui.components;

import org.speac.ui.internal.Stylesheet;
import org.speac.utilities.UIUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CleanScrollBarUI extends BasicScrollBarUI {
	private final Stylesheet currentStylesheet;

	public CleanScrollBarUI(Stylesheet currentStylesheet) {
		super();
		this.currentStylesheet = currentStylesheet;
	}

	@Override protected void configureScrollBarColors() {
		this.thumbColor				= this.currentStylesheet.bordersColor();
		this.thumbDarkShadowColor	= this.currentStylesheet.bordersColor();
		this.thumbHighlightColor	= this.currentStylesheet.bordersColor();
		this.thumbLightShadowColor	= this.currentStylesheet.bordersColor();
		this.trackColor				= this.currentStylesheet.backgroundColor();
		this.trackHighlightColor	= this.currentStylesheet.backgroundColor();
	}

	@Override protected JButton createDecreaseButton(int orientation) {
		return UIUtils.createEmptyButton(this.currentStylesheet.backgroundColor());
	}

	@Override protected JButton createIncreaseButton(int orientation) {
		return UIUtils.createEmptyButton(this.currentStylesheet.backgroundColor());
	}
}
