package sneer.bricks.skin.rooms;

import basis.brickness.Brick;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface ActiveRoomKeeper {
	
	Signal<String> room();
	Consumer<String> setter();

}
