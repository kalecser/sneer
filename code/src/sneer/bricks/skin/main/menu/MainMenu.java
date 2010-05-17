package sneer.bricks.skin.main.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface MainMenu extends MenuGroup<JMenu> {

	JMenuBar getMenuBarWidget();

}