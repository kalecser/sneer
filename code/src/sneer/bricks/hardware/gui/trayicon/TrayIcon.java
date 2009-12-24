package sneer.bricks.hardware.gui.trayicon;

import sneer.bricks.hardware.gui.actions.Action;

public interface TrayIcon {

	void addAction(Action action);
	void setDefaultAction(Action defaultAction);
	void clearActions();

	void messageBalloon(String title, String message);
	
	void dispose();

}
