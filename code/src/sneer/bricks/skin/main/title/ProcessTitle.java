package sneer.bricks.skin.main.title;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface ProcessTitle {

	public Signal<String> title();
	
}
