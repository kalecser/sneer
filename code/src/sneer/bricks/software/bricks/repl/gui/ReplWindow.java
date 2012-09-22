package sneer.bricks.software.bricks.repl.gui;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.software.bricks.snapploader.Snapp;
import basis.brickness.Brick;

@Snapp
@Brick(GUI.class)
public interface ReplWindow {

	public void open();
	
}
