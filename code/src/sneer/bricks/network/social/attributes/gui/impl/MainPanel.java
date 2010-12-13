package sneer.bricks.network.social.attributes.gui.impl;

import javax.swing.JPanel;

import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.lang.Consumer;

class MainPanel extends JPanel {

	MainPanel(final Attributes controller) {
		controller.myAttributes().addReceiver(new Consumer<CollectionChange<Attribute<?>>>() { @Override public void consume(CollectionChange<Attribute<?>> attributes) {
			for (Attribute<?> attribute : attributes.elementsAdded().toArray(new Attribute<?>[0])) {
				add(new AttributePanel(controller, attribute));
			}
		}});
	}

}
