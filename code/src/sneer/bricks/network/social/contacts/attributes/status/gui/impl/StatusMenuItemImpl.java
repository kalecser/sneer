package sneer.bricks.network.social.contacts.attributes.status.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import sneer.bricks.network.social.contacts.attributes.status.StatusFactory.Status;
import sneer.bricks.network.social.contacts.attributes.status.gui.StatusMenuItem;
import sneer.bricks.network.social.contacts.attributes.status.publisher.StatusPublisher;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;

class StatusMenuItemImpl implements StatusMenuItem {

	{
		MenuGroup<JMenu> statusSubmenu = my(MenuFactory.class).createMenuGroup("Status");
		ButtonGroup statusGroup = new ButtonGroup();
		for (Status status : Status.values()) {
			JRadioButtonMenuItem statusMenuItem = new JRadioButtonMenuItem(status.toString());
			statusMenuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent event) {
				my(StatusPublisher.class).publish(event.getActionCommand());
			}});
			statusGroup.add(statusMenuItem);
			if (status.equals(Status.ONLINE)) statusMenuItem.setSelected(true);
			statusSubmenu.getWidget().add(statusMenuItem);
		}
		statusSubmenu.getWidget().addSeparator();
		my(MainMenu.class).addGroup(statusSubmenu);
	}

}
