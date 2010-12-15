package sneer.bricks.network.social.attributes.gui;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.PickyConsumer;

public interface AttributePanelElement {

	String name();

	Signal<String> value();

	PickyConsumer<String> valueSetter();

}
