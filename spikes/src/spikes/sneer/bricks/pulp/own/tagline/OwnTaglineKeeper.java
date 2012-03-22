package spikes.sneer.bricks.pulp.own.tagline;

import basis.brickness.Brick;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface OwnTaglineKeeper {

	Signal<String> tagline();

	Consumer<String> taglineSetter();

}
