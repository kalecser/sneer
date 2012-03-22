package sneer.bricks.skin.widgets.reactive.impl;

import java.awt.Component;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import basis.lang.CacheMap;
import basis.lang.Producer;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.widgets.reactive.LabelProvider;


class RListCellRenderer<ELEMENT> implements ListCellRenderer {

	private final DefaultListCellRenderer _renderer = new DefaultListCellRenderer();
	private final LabelProvider<ELEMENT> _labelProvider;

	//Fix: This is a leak. Only elements actually being rendered by the JList should be kept. Not all elements that ever appeared on the list.
	private final CacheMap<ELEMENT, Signal<String>> _textsByElement = CacheMap.newInstance();
	private final CacheMap<ELEMENT, Signal<? extends Image>> _imagesByElement = CacheMap.newInstance();
	private final CacheMap<Image, ImageIcon> _iconsByImage = CacheMap.newInstance();

	
	RListCellRenderer(LabelProvider<ELEMENT> labelProvider) {
		_labelProvider = labelProvider;
	}
	
	
	@Override
	public Component getListCellRendererComponent(JList jList, Object elementObject, int index, boolean isSelected, boolean cellHasFocus) {
		ELEMENT element = (ELEMENT)elementObject;
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


	private Image imageFor(final ELEMENT element) {
		Signal<? extends Image> result = _imagesByElement.get(element, new Producer<Signal<? extends Image>>() { @Override public Signal<? extends Image> produce() {
			return _labelProvider.imageFor(element);
		}});
		return result.currentValue();
	}


	private String textFor(final ELEMENT element) {
		Signal<String> result = _textsByElement.get(element, new Producer<Signal<String>>() { @Override public Signal<String> produce() {
			return _labelProvider.textFor(element);
		}});
		return result.currentValue();
	}
	
}