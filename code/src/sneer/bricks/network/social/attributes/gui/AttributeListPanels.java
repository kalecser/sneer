package sneer.bricks.network.social.attributes.gui;

import java.awt.Component;

import basis.brickness.Brick;

import sneer.bricks.pulp.reactive.collections.CollectionSignal;

@Brick
public interface AttributeListPanels {

	Component produce(CollectionSignal<AttributePanelElement> elements);

}