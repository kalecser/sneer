package sneer.bricks.softwaresharing.gui.impl;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

class BrickListCellRenderer extends DefaultListCellRenderer {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Object> jList, Object element, int ignored2, boolean isSelected, boolean cellHasFocus) {
		FileVersionWrapper wrapper = (FileVersionWrapper) element;
		JLabel result = (JLabel) super.getListCellRendererComponent(jList, element, ignored2, isSelected, cellHasFocus);
		result.setIcon(wrapper.getIcon());
		return result;
	}
}