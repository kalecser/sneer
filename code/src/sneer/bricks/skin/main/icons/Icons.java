package sneer.bricks.skin.main.icons;

import javax.swing.Icon;

import basis.brickness.Brick;

import sneer.bricks.hardware.gui.nature.GUI;

@Brick(GUI.class)
public interface Icons {
	Icon load(Class<?> resourceBase, String resourceName) ;
}
