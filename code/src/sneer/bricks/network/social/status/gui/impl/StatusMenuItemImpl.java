package sneer.bricks.network.social.status.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.status.Status;
import sneer.bricks.network.social.status.StatusFactory;
import sneer.bricks.network.social.status.gui.StatusMenuItem;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.environments.Environment;

class StatusMenuItemImpl implements StatusMenuItem {

	{
		MenuGroup<JMenu> statusSubmenu = my(MenuFactory.class).createMenuGroup("Status");
		ButtonGroup statusGroup = new ButtonGroup();
		for (StatusFactory.Status status : StatusFactory.Status.values()) {
			final String statusName = status.name();
			JRadioButtonMenuItem statusMenuItem = new JRadioButtonMenuItem(statusName);
			statusMenuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent event) {
				System.out.println(my(Environment.class));
				my(Attributes.class).myAttributeSetter(Status.class).consume(statusName);
			}});
			statusGroup.add(statusMenuItem);
			if (status.equals(StatusFactory.DEFAULT_STATUS)) statusMenuItem.setSelected(true);
			statusSubmenu.getWidget().add(statusMenuItem);
		}
		statusSubmenu.getWidget().addSeparator(); // Fix: innocuous while using Synth LAF
		my(MainMenu.class).menu().addGroup(0, statusSubmenu);
	}

}
