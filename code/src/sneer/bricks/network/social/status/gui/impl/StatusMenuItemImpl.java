package sneer.bricks.network.social.status.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import sneer.bricks.network.social.status.gui.StatusMenuItem;
import sneer.bricks.network.social.status.protocol.StatusFactory;
import sneer.bricks.network.social.status.server.StatusPublisher;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;

class StatusMenuItemImpl implements StatusMenuItem {

	{
		MenuGroup<JMenu> statusSubmenu = my(MenuFactory.class).createMenuGroup("Status");
		ButtonGroup statusGroup = new ButtonGroup();
		for (String status : my(StatusFactory.class).values()) {
			JRadioButtonMenuItem statusMenuItem = new JRadioButtonMenuItem(status);
			statusMenuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent event) {
				my(StatusPublisher.class).publish(event.getActionCommand());
			}});
			statusGroup.add(statusMenuItem);
			if (status.equals("ONLINE")) statusMenuItem.setSelected(true);
			statusSubmenu.getWidget().add(statusMenuItem);
		}
		statusSubmenu.getWidget().addSeparator();
		my(MainMenu.class).addGroup(statusSubmenu);
	}

}
