package sneer.bricks.identity.seals;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface OwnSeal {

	Signal<Seal> get();

}
