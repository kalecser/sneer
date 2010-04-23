package sneer.bricks.hardware.gui.trayicon.impl;

import java.net.URL;

import sneer.bricks.hardware.gui.trayicon.SystemTrayNotSupported;
import sneer.bricks.hardware.gui.trayicon.TrayIcon;
import sneer.bricks.hardware.gui.trayicon.TrayIcons;
import sneer.bricks.pulp.reactive.Signal;

class TrayIconsImpl implements TrayIcons {

	@Override
	public TrayIcon newTrayIcon(URL icon, Signal<String> tooltip) throws SystemTrayNotSupported {
		return new TrayIconImpl(icon, tooltip);
	}
}
