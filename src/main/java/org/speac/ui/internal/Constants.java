package org.speac.ui.internal;

import org.speac.Start;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.*;

public final class Constants {
	public static final int FONT_HEIGHT = 20;
	public static final Image SPEAC_ICON = new ImageIcon(Start.RESOURCES_DIRECTORY.resolve("logo.png").toString()).getImage();
	public static final Font MAIN_FONT = new Font("JetBrains Mono NL", Font.PLAIN, Constants.FONT_HEIGHT);

	public static final double DEFAULT_ASPECT_RATIO = 1400./950.; // When opened the window will have this ratio
	public static final double FILLING_INDEX = 2./3.; // How much of the screen will be filled by the window
	public static final int LABELS_HEIGHT = 50;
	public static final int INPUTS_HEIGHT = 50;

	public static final Border DEFAULT_BUTTON_BORDER = new CompoundBorder(new MetalBorders.ButtonBorder(), new BasicBorders.MarginBorder());
}
