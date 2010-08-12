package sneer.bricks.hardware.gui.trayicon.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.security.InvalidParameterException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.actions.Action;
import sneer.bricks.hardware.gui.images.Images;
import sneer.bricks.hardware.gui.trayicon.SystemTrayNotSupported;
import sneer.bricks.hardware.gui.trayicon.TrayIcon;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class TrayIconImpl implements TrayIcon {

	private final java.awt.TrayIcon _trayIcon;

	private Action _defaultAction;
	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGc;


	TrayIconImpl(URL icon, Signal<String> tooltip) throws SystemTrayNotSupported {
		if (icon == null)
			throw new InvalidParameterException("Icon cannot be null");

		if (!SystemTray.isSupported())
			throw new SystemTrayNotSupported();

		SystemTray tray = SystemTray.getSystemTray();
		Image image = my(Images.class).getImage(icon);
		final java.awt.TrayIcon trayIcon = createTrayIcon(image);
		_refToAvoidGc = tooltip.addReceiver(new Consumer<String>() { @Override	public void consume(String text) {
			trayIcon.setToolTip(text); }});
		// trayIcon.addMouseListener(mouseListener);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			throw new SystemTrayNotSupported();
		}

		_trayIcon = trayIcon;
	}

	private java.awt.TrayIcon createTrayIcon(Image image) {
		java.awt.TrayIcon result = new java.awt.TrayIcon(image, null,
				new PopupMenu());
		
		result.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1){
					if (_defaultAction != null)
						_defaultAction.run();
				}
			}
		});
		result.setImageAutoSize(false);
		return result;
	}

	
	@Override
	public void addAction(final Action action) {
		PopupMenu popup = _trayIcon.getPopupMenu();
		if (popup.getItemCount() > 0)
			popup.addSeparator();

		final MenuItem menuItem = new MenuItem(action.caption());
		
		menuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			my(ExceptionHandler.class).shield(new Closure() { @Override public void run() {
				action.run();
			}});
		}});
		popup.add(menuItem);
	}

	@Override
	public void messageBalloon(String title, String message) {
		_trayIcon.displayMessage(title, message, MessageType.NONE);
	}
	
	@Override
	public void clearActions(){
		_trayIcon.getPopupMenu().removeAll();
	}

	@Override
	public void setDefaultAction(Action defaultAction) {
		_defaultAction = defaultAction;
	}

	@Override
	public void dispose() {
		SystemTray.getSystemTray().remove(_trayIcon);		
	}

}
