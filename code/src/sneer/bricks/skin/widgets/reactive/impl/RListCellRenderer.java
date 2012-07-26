package sneer.bricks.skin.widgets.reactive.impl;

import java.awt.Component;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import sneer.bricks.skin.widgets.reactive.LabelProvider;
import basis.lang.CacheMap;
import basis.lang.Producer;


class RListCellRenderer<ELEMENT> implements ListCellRenderer<ELEMENT> {

	private final DefaultListCellRenderer _renderer = new DefaultListCellRenderer();
	private final LabelProvider<ELEMENT> _labelProvider;

	private final CacheMap<Image, ImageIcon> _iconsByImage = CacheMap.newInstance();

	
	RListCellRenderer(LabelProvider<ELEMENT> labelProvider) {
		_labelProvider = labelProvider;
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList<? extends ELEMENT> jList, ELEMENT element, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel)_renderer.getListCellRendererComponent(jList, element, index, isSelected, cellHasFocus);

		ImageIcon icon = iconFor(element);
		String    text = textFor(element);

		if (icon != null) label.setIcon(icon);
		if (text != null) label.setText(text);

		return label;
	}


	private ImageIcon iconFor(final ELEMENT element) {
		final Image image = imageFor(element);
		if (image == null) return null;

		return _iconsByImage.get(image, new Producer<ImageIcon>() { @Override public ImageIcon produce() {
			return new ImageIcon(image);
		}});
	}


	private Image imageFor(ELEMENT element) {
		return _labelProvider.imageFor(element).currentValue();
	}


	private String textFor(ELEMENT element) {
		return _labelProvider.textFor(element).currentValue();
	}

	
}