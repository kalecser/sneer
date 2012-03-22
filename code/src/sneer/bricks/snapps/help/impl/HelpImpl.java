package sneer.bricks.snapps.help.impl;

import static basis.environments.Environments.my;

import javax.swing.JOptionPane;

import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.snapps.help.Help;

class HelpImpl implements Help {

	{
		my(MainMenu.class).menu().addAction(10, "Help!", new Runnable() { @Override public void run() {
			String message =
				" If you want help to USE Sneer, the best thing\n" +
				" is to get a Sovereign friend to help you.\n" +
				"\n" +
				" For help on DEVELOPING bricks, refer to the\n" +
				" README.txt file in the sneer/code/docs folder.";
				
			JOptionPane.showMessageDialog(null, message, "Help", JOptionPane.INFORMATION_MESSAGE);
		}});
	}
	
}
