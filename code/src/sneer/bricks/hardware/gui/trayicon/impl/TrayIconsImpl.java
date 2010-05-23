package sneer.bricks.hardware.gui.trayicon.impl;

import java.net.URL;

import sneer.bricks.hardware.gui.trayicon.SystemTrayNotSupported;
import sneer.bricks.hardware.gui.trayicon.TrayIcon;
import sneer.bricks.hardware.gui.trayicon.TrayIcons;
import sneer.bricks.pulp.reactive.Signal;

class TrayIconsImpl implements TrayIcons {

	private TrayIconImpl _trayIcon;

	@Override
	public TrayIcon newTrayIcon(URL icon, Signal<String> tooltip) throws SystemTrayNotSupported {
		
		if (_trayIcon != null){
			throw new IllegalStateException("Trying to open more than one tray icon");
		}
		
		_trayIcon = new TrayIconImpl(icon, tooltip);
		return _trayIcon;
	}

	@Override
	public void messageBalloon(String title, String message) {
		if (_trayIcon == null){
			return;
		}
		
		_trayIcon.messageBalloon(title, message);
	}
}
