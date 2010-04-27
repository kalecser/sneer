package sneer.bricks.identity.keys.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.keys.Keys;
import sneer.bricks.software.folderconfig.tests.BrickTest;


public class KeysTest extends BrickTest {

	Keys _subject = my(Keys.class);
	
	@Test
	public void keyGeneration() {
		_subject.generateKeyPair("long passphrase 123456789012345678901234567890123456789012345678901234567890");
		assertNotNull(_subject.ownPublicKey().currentValue());
	}
	
}
