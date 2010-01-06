package sneer.bricks.pulp.blinkinglights;

import sneer.bricks.pulp.reactive.Signal;

public interface Light {

	Signal<Boolean> isOn();
	
	LightType type();
	String caption();
	Throwable error();
	String helpMessage();
	
}
