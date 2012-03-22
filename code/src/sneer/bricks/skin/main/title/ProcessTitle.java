package sneer.bricks.skin.main.title;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface ProcessTitle {

	public Signal<String> title();
	
}
