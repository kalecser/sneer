package sneer.bricks.snapps.chat.gui.panels;

import java.awt.Image;

public interface Message {

	Image avatar();
	
	String author();
	boolean isByMe();
	
	long time();
	
	String text();

}
