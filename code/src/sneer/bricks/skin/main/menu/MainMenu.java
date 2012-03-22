package sneer.bricks.skin.main.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import basis.brickness.Brick;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.skin.menu.MenuGroup;

@Brick(GUI.class)
public interface MainMenu {

	JMenuBar getMenuBarWidget();
	MenuGroup<JMenu> menu();

}