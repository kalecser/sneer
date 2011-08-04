package sneer.bricks.skin.main.icons;

import javax.swing.Icon;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface Icons {
	Icon load(Class<?> resourceBase, String resourceName) ;
}
