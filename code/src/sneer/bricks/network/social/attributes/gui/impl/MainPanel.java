package sneer.bricks.network.social.attributes.gui.impl;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Consumer;

import static sneer.foundation.environments.Environments.my;

class MainPanel extends Box {

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	private final SetSignal<Attribute<?>> _attributes;

	MainPanel() {
		super(BoxLayout.Y_AXIS);
		_attributes = my(Attributes.class).myAttributes();

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		_toAvoidGC = _attributes.addReceiver(new Consumer<CollectionChange<Attribute<?>>>() { @Override public void consume(CollectionChange<Attribute<?>> changes) {
			addNewAttributePanels(changes.elementsAdded().toArray(new Attribute<?>[0]));
		}});
	}

	private void addNewAttributePanels(Attribute<?>[] attributes) {
		for (Attribute<?> attribute : attributes)
			add(newAttributePanelFor(attribute));
		smartPack();
	}

	private void smartPack() {
		Container ancestor = getTopLevelAncestor();
		if (ancestor == null) return; // Don't you just love AWT?
		((Window) ancestor).pack(); // Fix: Is there another way?
	}

	
	private JPanel newAttributePanelFor(Attribute<?> attribute) {
		JPanel result = new AttributePanel(attribute);
		result.setMaximumSize(new Dimension(340, 60));
		result.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
		result.setAlignmentX(CENTER_ALIGNMENT);
		result.setAlignmentY(CENTER_ALIGNMENT);
		return result;
	}

}
