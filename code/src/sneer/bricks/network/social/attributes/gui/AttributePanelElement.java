package sneer.bricks.network.social.attributes.gui;

import basis.lang.PickyConsumer;
import sneer.bricks.pulp.reactive.Signal;

public interface AttributePanelElement {

	String name();

	Signal<String> value();

	PickyConsumer<String> valueSetter();

}
