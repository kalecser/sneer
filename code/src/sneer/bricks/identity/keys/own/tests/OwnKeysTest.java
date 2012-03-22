package sneer.bricks.identity.keys.own.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class OwnKeysTest extends BrickTestBase {

	private static final String PASSPHRASE = "long passphrase 123456789012345678901234567890123456789012345678901234567890";
//	private static final int NUM_RECREATIONS = 10;
	OwnKeys _subject = my(OwnKeys.class);
	
	@Test
	public void keyGeneration() {
		_subject.generateKeyPair(PASSPHRASE.getBytes());
		assertNotNull(_subject.ownPublicKey().currentValue());
	}
	
/*	@Test
	public void recreateKey() {
		_subject.generateKeyPair(PASSPHRASE.getBytes());
		for (int i = 0; i < NUM_RECREATIONS; i++) {
			PublicKey previous = _subject.ownPublicKey().currentValue();
			_subject.generateKeyPair(PASSPHRASE.getBytes());
			assertArrayEquals(previous.getEncoded(), _subject.ownPublicKey().currentValue().getEncoded());
		}
	}
*/	
}
