package sneer.bricks.hardware.cpu.crypto.impl;

import static sneer.foundation.environments.Environments.my;

import java.security.MessageDigest;

import sneer.bricks.hardware.cpu.crypto.Digester;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;

class DigesterImpl implements Digester {

	private MessageDigest _sha512;

	DigesterImpl(MessageDigest sha512) {
		_sha512 = sha512;
	}

	@Override
	public void update(byte[] bytes) {
		_sha512.update(bytes);
	}

	@Override
	public void update(byte[] bytes, int offset, int length) {
		_sha512.update(bytes, offset, length);
	}

	@Override
	public Hash digest() {
		return wrap(_sha512.digest());
	}

	@Override
	public Hash digest(byte[] bytes) {
		return wrap(_sha512.digest(bytes));
	}

	private Hash wrap(byte[] bytes) {
		return new Hash(my(ImmutableArrays.class).newImmutableByteArray(bytes));
	}

}
