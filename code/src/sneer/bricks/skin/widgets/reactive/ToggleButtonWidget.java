package sneer.bricks.skin.widgets.reactive;

import javax.swing.JToggleButton;

import basis.lang.PickyConsumer;

import sneer.bricks.pulp.reactive.Signal;

public interface ToggleButtonWidget<WIDGET extends JToggleButton> extends ComponentWidget<WIDGET> {

	Signal<Boolean> output();

	PickyConsumer<Boolean> setter();

}
