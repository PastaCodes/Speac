package org.speac.ui.internal;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class Style {
	public static class InvalidHexColorException extends Exception {
		public InvalidHexColorException(String message) {
			super(message);
		}
	}

	private final String asCss;
	private final SimpleAttributeSet asAttributeSet;

	public Style(String foregroundColor, boolean bold, boolean italic, boolean underlined) throws InvalidHexColorException {
		Style.validateHexColor(foregroundColor);

		String css = "";

		css += "color: " + foregroundColor + ";";
		if (bold)
			css += "font-weight: bold;";
		if (italic)
			css += "font-style: italic;";
		if (underlined)
			css += "text-decoration: underlined;";

		this.asCss = css;

		this.asAttributeSet = new SimpleAttributeSet();

		StyleConstants.setForeground(this.asAttributeSet, Color.decode(foregroundColor));
		if (bold)
			StyleConstants.setBold(this.asAttributeSet, true);
		if (italic)
			StyleConstants.setItalic(this.asAttributeSet, true);
		if (underlined)
			StyleConstants.setUnderline(this.asAttributeSet, true);
	}
	public Style(String foregroundColor) throws InvalidHexColorException {
		this(foregroundColor, false, false, false);
	}

	public String asCss() {
		return this.asCss;
	}

	public AttributeSet asAttributeSet() {
		return new SimpleAttributeSet(this.asAttributeSet);
	}

	private static void validateHexColor(String hex) throws InvalidHexColorException {
		try {
			Color.decode(hex);
		} catch (NumberFormatException exception) {
			throw new InvalidHexColorException("'" + hex + "' is not a valid hex color");
		}
	}
}
