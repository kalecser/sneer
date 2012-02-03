package sneer.bricks.identity.keys.gui.impl.tests;

import org.junit.Assert;
import org.junit.Test;

import sneer.bricks.identity.keys.gui.impl.PublicKeyInitDialogImpl;

public class PassphraseTest extends Assert {

	@Test
	public void passphrases() {
		assertEquals("avidacomo2mas", PublicKeyInitDialogImpl.normalize("A Vida é como 2 Maçãs..."));
	}
	
}
