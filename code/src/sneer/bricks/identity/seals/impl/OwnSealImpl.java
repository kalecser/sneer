package sneer.bricks.identity.seals.impl;

import static basis.environments.Environments.my;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Arrays;

import basis.lang.Functor;
import basis.lang.arrays.ImmutableByteArray;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class OwnSealImpl implements OwnSeal {

	final Signal<Seal> _cache = init();
	
	
	@Override
	public Signal<Seal> get() {
		return _cache;
	}

	
	private Signal<Seal> init() {
		return adapt(my(OwnKeys.class).ownPublicKey());
	}


	private Signal<Seal> adapt(Signal<PublicKey> ownPublicKey) {
		return my(Signals.class).adapt(ownPublicKey, new Functor<PublicKey, Seal>() { @Override public Seal evaluate(PublicKey publicKey) {
			return publicKey == null
				? newTemporarySealForTests()
				: hash(publicKey);
		}});
	}

	
	private Seal hash(PublicKey publicKey) {
		Hash result = my(Crypto.class).digest(publicKey.getEncoded());
		return new Seal(new ImmutableByteArray(result.bytes.copy()));
	}

	
	private Seal newTemporarySealForTests() {
		if (!"true".equals(System.getProperty("sneer.testmode"))) throw new IllegalStateException("Internal Sneer Error: Public Key should have been generated already. Please report this error to the Sneer team.");
		try {
			byte[] nanoTime = Long.toHexString(System.nanoTime()).getBytes("UTF-8");
			byte[] bytes = Arrays.copyOf(nanoTime, Seal.SIZE_IN_BYTES);
			return new Seal(new ImmutableByteArray(bytes));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}
