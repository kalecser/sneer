package sneer.bricks.identity.keys.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import sneer.bricks.identity.keys.gui.PublicKeyDialog;
import sneer.bricks.identity.keys.own.OwnKeys;

public class PublicKeyDialogImpl implements PublicKeyDialog {

	@Override
	public void initPublicKeyIfNecessary() {
		if (my(OwnKeys.class).ownPublicKey().currentValue() == null)
			initPublicKey();
	}

	
	private void initPublicKey() {
		String seed = "true".equals(System.getProperty("sneer.dummy"))
			? "Dummy"
			: promptForPassphrase();
			
		my(OwnKeys.class).generateKeyPair(utf8(seed));
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

	
	private byte[] utf8(String seed) {
		try {
			return seed.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
