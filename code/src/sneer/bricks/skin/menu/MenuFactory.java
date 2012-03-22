package sneer.bricks.skin.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

import basis.brickness.Brick;

import sneer.bricks.hardware.gui.nature.GUI;

@Brick(GUI.class)
public interface MenuFactory {

	MenuGroup<JMenuBar> createMenuBar();
	MenuGroup<JMenu> createMenuGroup(String name);
	MenuGroup<JPopupMenu> createPopupMenu();
}