package sneer.bricks.snapps.contacts.gui.info;

import basis.brickness.Brick;
import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Snapp
@Brick(GUI.class)
public interface ContactInfoWindow {

	public void open();
}
