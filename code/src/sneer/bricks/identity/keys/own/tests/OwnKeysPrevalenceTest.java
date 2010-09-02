package sneer.bricks.identity.keys.own.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;

public class OwnKeysPrevalenceTest extends BrickTestWithFiles {

	private static final String PASSPHRASE = "long passphrase 123456789012345678901234567890123456789012345678901234567890";
	private static final int NUM_RUNS = 10;

	@Test
	public void recoversFromPrevalence() {
		my(OwnKeys.class).generateKeyPair(PASSPHRASE.getBytes());
		final byte[] encoded = my(OwnKeys.class).ownPublicKey().currentValue().getEncoded();
		for (int i = 0; i < NUM_RUNS; i++) {
			Environments.runWith(newTestEnvironment(my(OwnKeys.class)), new Closure() { @Override public void run() {
				assertArrayEquals(encoded, my(OwnKeys.class).ownPublicKey().currentValue().getEncoded());
			}});
			assertArrayEquals(encoded, my(OwnKeys.class).ownPublicKey().currentValue().getEncoded());
		}
	}

	@Test
	public void recoversChangedFromPrevalence() {
		my(OwnKeys.class).generateKeyPair(PASSPHRASE.getBytes());
		final ByRef<byte[]> encoded = ByRef.newInstance(my(OwnKeys.class).ownPublicKey().currentValue().getEncoded());
		for (int i = 0; i < NUM_RUNS; i++) {
			Environments.runWith(newTestEnvironment(my(OwnKeys.class)), new Closure() { @Override public void run() {
				assertArrayEquals(encoded.value, my(OwnKeys.class).ownPublicKey().currentValue().getEncoded());
				my(OwnKeys.class).generateKeyPair(PASSPHRASE.getBytes());
				encoded.value = my(OwnKeys.class).ownPublicKey().currentValue().getEncoded();
			}});
		}
	}

}
