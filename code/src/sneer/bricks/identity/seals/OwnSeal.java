package sneer.bricks.identity.seals;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface OwnSeal {

	Signal<Seal> get();

}
