package sneer.bricks.identity.keys.own.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.software.folderconfig.tests.BrickTest;


public class OwnKeysTest extends BrickTest {

	OwnKeys _subject = my(OwnKeys.class);
	
	@Test
	public void keyGeneration() {
		_subject.generateKeyPair("long passphrase 123456789012345678901234567890123456789012345678901234567890".getBytes());
		assertNotNull(_subject.ownPublicKey().currentValue());
	}
	
}
