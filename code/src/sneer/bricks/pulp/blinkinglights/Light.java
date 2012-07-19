package sneer.bricks.pulp.blinkinglights;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.ListSignal;

public interface Light {

	Signal<Boolean> isOn();
	
	LightType type();
	Signal<String> caption();
	Throwable error();
	String helpMessage();

	ListSignal<Action> actions();
	public void addAction(Action action);
}
