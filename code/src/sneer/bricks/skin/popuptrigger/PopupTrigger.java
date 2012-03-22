package sneer.bricks.skin.popuptrigger;

import java.awt.Component;
import java.awt.event.MouseEvent;

import basis.brickness.Brick;
import basis.lang.Consumer;


@Brick
public interface PopupTrigger {
	
	void listen(Component sorce, Consumer<MouseEvent> receiver);
	
}
