package sneer.bricks.network.social.attributes.gui.impl;

import java.awt.Component;

import sneer.bricks.network.social.attributes.gui.AttributeListPanels;
import sneer.bricks.network.social.attributes.gui.AttributePanelElement;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;

class AttributeListPanelsImpl implements AttributeListPanels {

	@Override
	public Component produce(CollectionSignal<AttributePanelElement> elements) {
		return new AttributeListPanel(elements);
	}

}
