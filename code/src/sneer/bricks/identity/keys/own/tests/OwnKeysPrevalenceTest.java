package sneer.bricks.identity.keys.own.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class OwnKeysPrevalenceTest extends BrickTestBase {

	private static final String PASSPHRASE = "long passphrase 123456789012345678901234567890123456789012345678901234567890";
	private static final int NUM_RUNS = 4;

	
	@Test
	public void recoversFromPrevalence() {
		my(OwnKeys.class).generateKeyPair(PASSPHRASE.getBytes());
		final byte[] encoded = myPK();
		for (int i = 0; i < NUM_RUNS; i++) {
			Environments.runWith(newTestEnvironment(my(OwnKeys.class)), new Closure() { @Override public void run() {
				assertArrayEquals(encoded, myPK());
			}});
			assertArrayEquals(encoded, myPK());
		}
	}

	
/*	@Test
	public void recoversChangedFromPrevalence() {
		my(OwnKeys.class).generateKeyPair(PASSPHRASE.getBytes());
		final ByRef<byte[]> encoded = ByRef.newInstance(myPK());
		for (int i = 0; i < NUM_RUNS; i++) {
			Environments.runWith(newTestEnvironment(my(OwnKeys.class)), new Closure() { @Override public void run() {
				assertArrayEquals(encoded.value, myPK());
				my(OwnKeys.class).generateKeyPair(PASSPHRASE.getBytes());
				encoded.value = myPK();
			}});
		}
	}
*/
	private byte[] myPK() {
		return my(OwnKeys.class).ownPublicKey().currentValue().getEncoded();
	}

}
