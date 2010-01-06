package sneer.bricks.skin.main.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.brickness.Brick;

@Brick
public interface MainMenu extends MenuGroup<JMenu> {

	JMenuBar getMenuBarWidget();

}