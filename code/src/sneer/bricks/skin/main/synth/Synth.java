package sneer.bricks.skin.main.synth;

import javax.swing.Icon;
import javax.swing.JComponent;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface Synth {

	void load(Class<?> resourceBase) ;
	Icon load(Class<?> resourceBase, String resourceName) ;
	void attach(JComponent component);
	void attach(JComponent component, String synthName);
}
