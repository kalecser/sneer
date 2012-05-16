package sneer.bricks.snapps.chat.gui.panels;

import java.awt.Image;

public interface Message {

	String author();
	
	Image avatar();
	
	String text();
	
	long time();

}
