package sneer.bricks.skin.menu;

import javax.swing.JComponent;
import javax.swing.JMenu;

import sneer.bricks.hardware.gui.actions.Action;

public interface MenuGroup<T extends JComponent> {

	T getWidget();
	void addAction(Action action);
	void addAction(Action action, Integer index);
	void addAction(String caption, Runnable action);
	void addAction(String caption, Runnable action, Integer index);	
	void addGroup(MenuGroup<JMenu> group);
	void addGroup(MenuGroup<JMenu> group, Integer index);

}