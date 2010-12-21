package sneer.bricks.network.social.attributes.gui;

import java.awt.Component;

import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface AttributeListPanels {

	Component produce(CollectionSignal<AttributePanelElement> elements);

}