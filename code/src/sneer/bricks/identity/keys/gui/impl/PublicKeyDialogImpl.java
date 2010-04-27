package sneer.bricks.identity.keys.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JOptionPane;

import sneer.bricks.identity.keys.Keys;
import sneer.bricks.identity.keys.gui.PublicKeyDialog;

public class PublicKeyDialogImpl implements PublicKeyDialog {

	@Override
	public void initPublicKeyIfNecessary() {
		if (my(Keys.class).ownPublicKey().currentValue() == null)
			initPublicKey();
	}

	
	private void initPublicKey() {
		String seed = "true".equals(System.getProperty("sneer.dummy"))
			? "Dummy"
			: promptForPassphrase();
			
		my(Keys.class).generateKeyPair(seed);
	}

	
	private String promptForPassphrase() {
		while (true) {
			String result = JOptionPane.showInputDialog(
				" Enter a passphrase to generate your Sneer identity.\n\n" +
				" It should be something secret and unique to you. If\n" +
				" you remember it, you can recreate your Sneer identity\n" +
				" whenever you need it.");
			if (result == null) continue;
			if (result.isEmpty()) continue;
			return result;
		}
	}

}
