package sneer.bricks.identity.keys.gui.impl;

import static basis.environments.Environments.my;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.swing.JOptionPane;

import sneer.bricks.identity.keys.gui.PublicKeyInitDialog;
import sneer.bricks.identity.keys.own.OwnKeys;

public class PublicKeyInitDialogImpl implements PublicKeyInitDialog {

	{
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
				" Enter a passphrase to generate your Sovereign Seal (public key).\n\n" +
				" It should be something secret and unique to you. A phone number\n" +
				" from your childhood followed by a line from your favorite song is\n" +
				" a good example.\n\n" +
				" Remember it, so you can recreate your Seal whenever you need it.");
			if (result == null) System.exit(0);
			
			result = normalize(result);
			if (result.length() >= 30)
				return result;
			
			JOptionPane.showMessageDialog(null,
				" Your passphrase must be more than 30 alphanumeric (A-Z and 0-9)\n" +
				" characters long. All other characters are ignored.\n\n" +
				" Passphrases are not case sensitive: 'this' and 'THIS' are the same.");
		}
	}


	public static String normalize(String passphrase) {
		String result = "";
		for (int i = 0; i < passphrase.length(); i++)
			if (isAlphanumeric(passphrase.charAt(i)))
				result += passphrase.charAt(i);
			
		return result.toLowerCase(Locale.ENGLISH);
	}


	private static boolean isAlphanumeric(char c) {
		if (c >= '0' && c <= '9') return true;
		if (c >= 'A' && c <= 'Z') return true;
		if (c >= 'a' && c <= 'z') return true;
		return false;
	}


	private byte[] utf8(String seed) {
		try {
			return seed.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
