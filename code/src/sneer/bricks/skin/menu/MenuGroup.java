package sneer.bricks.skin.menu;

import javax.swing.JComponent;
import javax.swing.JMenu;

import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.pulp.reactive.Signal;

public interface MenuGroup<T extends JComponent> {

	T getWidget();
	void addAction(int positionInMenu, Action action);
	void addAction(int positionInMenu, String caption, Runnable action);	
	void addGroup(int positionInMenu, MenuGroup<JMenu> group);
	void addActionWithCheckBox(int positionInMenu, String caption, Signal<Boolean> checked, Runnable action);

}