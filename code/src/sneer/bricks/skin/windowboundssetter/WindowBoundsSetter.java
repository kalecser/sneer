package sneer.bricks.skin.windowboundssetter;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;

import basis.brickness.Brick;


@Brick
public interface WindowBoundsSetter {
	
	void setDefaultBaseComponent(Component baseComponent);

	Rectangle unusedArea();
	void setBestBounds(Window window);
	void setBestBounds(Window window, Component baseComponent);
	void setBestBounds(Window window, Component baseComponent,  int maxWidth);

	void runWhenBaseContainerIsReady(Runnable runnable);
}
