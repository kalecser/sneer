package spikes.sneer.pulp.things;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface ThingHome {

	Thing create(String name, String description);

	SetSignal<Thing> search(String tags);

}
