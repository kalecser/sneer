package spikes.sandro.summit.register;

import basis.brickness.Brick;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface SimpleRegister {

	Signal<String> output();

	Consumer<String> setter();

}
