package org.speac.utilities;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
	public static void center(JFrame frame) {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension paneDimension = frame.getSize();
		frame.setLocation(
				(screenDimension.width - paneDimension.width) / 2,
				(screenDimension.height - paneDimension.height) / 2
		);
	}

	public static JButton createEmptyButton(Color background) {
		JButton emptyButton = new JButton();
		emptyButton.setSize(new Dimension(0, 0));
		emptyButton.setBackground(background);
		emptyButton.setBorder(BorderFactory.createEmptyBorder());
		emptyButton.setEnabled(false);
		return emptyButton;
	}
}
