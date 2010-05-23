package sneer.bricks.skin.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface MenuFactory {

	MenuGroup<JMenuBar> createMenuBar();
	MenuGroup<JMenu> createMenuGroup(String name);
	MenuGroup<JPopupMenu> createPopupMenu();
}