package sneer.bricks.skin.widgets.autoscroll;

import javax.swing.JScrollPane;

import sneer.bricks.hardware.gui.nature.GUI;
import basis.brickness.Brick;

@Brick(GUI.class)
public interface AutoScroll {
	
	void autoscroll(JScrollPane subject);

}