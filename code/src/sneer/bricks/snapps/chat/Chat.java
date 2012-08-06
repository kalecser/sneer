package sneer.bricks.snapps.chat;

import basis.brickness.Brick;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Snapp //  Just commented to hide on Dashboard. It will return later as the new Chat.
@Brick
public interface Chat {

	Message convert(ChatMessage message);
	
}
