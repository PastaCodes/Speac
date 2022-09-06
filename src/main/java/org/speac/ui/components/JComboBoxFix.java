package org.speac.ui.components;

import org.speac.utilities.FixedList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Adds functionalities for styling JComboBox
 */
public class JComboBoxFix<E> extends JComboBox<E> {
	public static class JComboBoxPopupFix extends BasicComboPopup {
		protected JComboBoxPopupFix(JComboBox<Object> comboBox) {
			super(comboBox);
		}

		protected JScrollPane getScrollPane() {
			return this.scroller;
		}
	}

	public class JComboBoxUiFix extends BasicComboBoxUI {
		protected JComboBoxUiFix() {
			super();
		}

		@Override protected JButton createArrowButton() {
			if (JComboBoxFix.this.arrowButtonCreation == null)
				return super.createArrowButton();

			try {
				return JComboBoxFix.this.arrowButtonCreation.call();
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override protected ComboPopup createPopup() {
			return new JComboBoxPopupFix(this.comboBox);
		}

		@Override public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
			Component rendererComponent = this.comboBox.getRenderer().getListCellRendererComponent(
					this.listBox,
					this.comboBox.getSelectedItem(),
					-1,
					hasFocus && !isPopupVisible(this.comboBox),
					false);

			int x = bounds.x;
			int y = bounds.y;
			int w = bounds.width;
			int h = bounds.height;
			if (this.padding != null) {
				x += this.padding.left;
				y += this.padding.top;
				w -= this.padding.left + this.padding.right;
				h -= this.padding.top + this.padding.bottom;
			}

			if (this.currentValuePane.isBackgroundSet())
				rendererComponent.setBackground(this.currentValuePane.getBackground());

			this.currentValuePane.paintComponent(
					g,
					rendererComponent,
					comboBox,
					x, y, w, h,
					rendererComponent instanceof JPanel);

			if (JComboBoxFix.this.currentValueBorder != null)
				JComboBoxFix.this.currentValueBorder.paintBorder(
						rendererComponent,
						g,
						x, y, w, h);
		}

		protected JComboBoxPopupFix getPopup() {
			return (JComboBoxPopupFix) this.popup;
		}

		protected JButton getArrowButton() {
			return this.arrowButton;
		}

		protected void setCurrentValueBackground(Color currentValueBackground) {
			this.currentValuePane.setBackground(currentValueBackground);
		}
	}

	protected static final DefaultListCellRenderer DEFAULT_RENDERER = new DefaultListCellRenderer();

	public class JComboBoxRendererFix implements ListCellRenderer<E> {
		protected JComboBoxRendererFix() {
			super();
		}

		@Override public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel renderer = (JLabel) JComboBoxFix.DEFAULT_RENDERER.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (JComboBoxFix.this.cellBorder != null)
				renderer.setBorder(JComboBoxFix.this.cellBorder);

			if (JComboBoxFix.this.cellForegroundColor != null)
				renderer.setForeground(JComboBoxFix.this.cellForegroundColor);

			if (cellHasFocus || isSelected) {
				if (JComboBoxFix.this.selectedCellBackgroundColor != null)
					renderer.setBackground(JComboBoxFix.this.selectedCellBackgroundColor);
			} else {
				if (JComboBoxFix.this.cellBackgroundColor != null)
					renderer.setBackground(JComboBoxFix.this.cellBackgroundColor);
			}

			return renderer;
		}
	}

	private Callable<JButton> arrowButtonCreation;

	private Border currentValueBorder;
	private Border cellBorder;
	private Color cellForegroundColor;
	private Color cellBackgroundColor;
	private Color selectedCellBackgroundColor;

	public JComboBoxFix(FixedList<E> items) {
		super(items.toArray());
		this.setRenderer(new JComboBoxRendererFix());
		this.setUI(new JComboBoxUiFix());
	}

	public void update() {
		this.setUI(this.getUI());
	}

	protected JComboBoxUiFix getUIFix() {
		@SuppressWarnings("unchecked") JComboBoxUiFix uiFix = (JComboBoxUiFix) this.getUI();
		return uiFix;
	}

	public void setArrowButtonCreation(Callable<JButton> arrowButtonCreation) {
		this.arrowButtonCreation = arrowButtonCreation;
	}

	public void setCurrentValueBorder(Border currentValueBorder) {
		this.currentValueBorder = currentValueBorder;
	}

	public void setCurrentValueBackground(Color currentValueBackground) {
		this.getUIFix().setCurrentValueBackground(currentValueBackground);
	}

	public void setCellBorder(Border cellBorder) {
		this.cellBorder = cellBorder;
	}

	public void setCellForegroundColor(Color cellForegroundColor) {
		this.cellForegroundColor = cellForegroundColor;
	}

	public void setCellBackgroundColor(Color cellBackgroundColor) {
		this.cellBackgroundColor = cellBackgroundColor;
	}

	public void setSelectedCellBackgroundColor(Color selectedCellBackgroundColor) {
		this.selectedCellBackgroundColor = selectedCellBackgroundColor;
	}

	public JComboBoxPopupFix getPopup() {
		return this.getUIFix().getPopup();
	}

	public JButton getArrowButton() {
		return this.getUIFix().getArrowButton();
	}

	public JScrollBar getScrollBar() {
		return this.getPopup().getScrollPane().getVerticalScrollBar();
	}
}
