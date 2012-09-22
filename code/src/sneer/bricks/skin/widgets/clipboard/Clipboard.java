package sneer.bricks.skin.widgets.clipboard;

import basis.brickness.Brick;

@Brick
public interface Clipboard {

	void setContent(String cotent);
	
	String getContent();
}
