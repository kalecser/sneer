package sneer.bricks.skin.menu;

import javax.swing.JComponent;

import sneer.bricks.hardware.gui.actions.Action;

public interface MenuGroup<T extends JComponent> {

	T getWidget();
	void addAction(Action action);
	void addAction(String caption, Runnable action);	
	void addGroup(MenuGroup<? extends JComponent> group);
	
}