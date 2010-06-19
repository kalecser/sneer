package sneer.bricks.snapps.contacts.actions;

import sneer.bricks.hardware.gui.actions.Action;

public interface ContactAction extends Action {

	boolean isVisible();

	boolean isEnabled();

	int positionInMenu();

}
