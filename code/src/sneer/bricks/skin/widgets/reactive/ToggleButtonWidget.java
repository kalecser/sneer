package sneer.bricks.skin.widgets.reactive;

import javax.swing.JToggleButton;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.PickyConsumer;

public interface ToggleButtonWidget<WIDGET extends JToggleButton> extends ComponentWidget<WIDGET> {

	Signal<Boolean> output();

	PickyConsumer<Boolean> setter();

}
