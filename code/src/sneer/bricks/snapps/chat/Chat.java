package sneer.bricks.snapps.chat;

import basis.brickness.Brick;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Snapp
@Brick
public interface Chat {

	Message convert(ChatMessage message);
	
}
