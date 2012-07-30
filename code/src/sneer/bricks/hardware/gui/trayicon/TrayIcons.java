package sneer.bricks.hardware.gui.trayicon;

import java.net.URL;

import basis.brickness.Brick;
import basis.lang.Closure;

import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface TrayIcons {

	TrayIcon initTrayIcon(URL userIcon, Signal<String> tooltip) throws SystemTrayNotSupported;

	void messageBalloon(String title, String message);

	void addActionListener(Closure closure);

}