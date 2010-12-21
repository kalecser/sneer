package sneer.bricks.network.social.attributes.gui.impl;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.attributes.gui.AttributePanelElement;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.foundation.lang.Consumer;

class AttributeListPanel extends Box {

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	private final CollectionSignal<AttributePanelElement> _attributes;

	AttributeListPanel(CollectionSignal<AttributePanelElement> elements) {
		super(BoxLayout.Y_AXIS);
		_attributes = elements;

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		_toAvoidGC = elements.addReceiver(new Consumer<Object>() { @Override public void consume(Object ignored) {
			refresh();
		}});
	}

	private void refresh() {
		removeAll();
		for (AttributePanelElement element : _attributes)
			add(newAttributePanelFor(element));
		smartPack();
	}

	private void smartPack() {
		Container ancestor = getTopLevelAncestor();
		if (ancestor == null) return; // Don't you just love AWT?
		((Window) ancestor).pack(); // Fix: Is there another way?
	}

	
	private JPanel newAttributePanelFor(AttributePanelElement attribute) {
		JPanel result = new AttributePanel(attribute);
		result.setMaximumSize(new Dimension(340, 60));
		result.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
		result.setAlignmentX(CENTER_ALIGNMENT);
		result.setAlignmentY(CENTER_ALIGNMENT);
		return result;
	}

}
