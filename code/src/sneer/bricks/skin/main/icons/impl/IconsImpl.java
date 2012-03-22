package sneer.bricks.skin.main.icons.impl;

import static basis.environments.Environments.my;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.skin.main.icons.Icons;

class IconsImpl implements Icons {
	
	@Override
	public Icon load(final Class<?> resourceBase, final String resourceName){
		my(GuiThread.class).assertInGuiThread();
		URL path = resourceBase.getResource(resourceName);
		return new ImageIcon(path);	
	}

}