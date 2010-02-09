package sneer.bricks.hardware.cpu.crypto.impl;

import java.security.MessageDigest;

import sneer.bricks.hardware.cpu.crypto.Digester;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;

class DigesterImpl implements Digester {

	private MessageDigest _sha512;
	private MessageDigest _whirlPool;

	DigesterImpl(MessageDigest sha512, MessageDigest whirlPool) {
		_sha512 = sha512;
		_whirlPool = whirlPool;
	}

	@Override
	public void update(byte[] bytes) {
		_sha512.update(bytes);
		_whirlPool.update(bytes);
	}

	@Override
	public void update(byte[] bytes, int offset, int length) {
		_sha512.update(bytes, offset, length);
		_whirlPool.update(bytes, offset, length);
	}

	@Override
	public Sneer1024 digest() {
		byte[] sha512 = _sha512.digest();
		byte[] whirlPool = _whirlPool.digest();
		return wrap(merge(sha512, whirlPool));
	}

	@Override
	public Sneer1024 digest(byte[] bytes) {
		byte[] sha512 = _sha512.digest(bytes);
		byte[] whirlPool = _whirlPool.digest(bytes);
		byte[] result = merge(sha512, whirlPool); 
		return wrap(result);
	}

	@Override
	public void reset() {
		_sha512.reset();
		_whirlPool.reset();
	}

	private Sneer1024 wrap(byte[] sneer1024Bytes) {
		return new Sneer1024Impl(sneer1024Bytes);
	}

	byte[] merge(byte[] sha512, byte[] whirlPool) {
		byte[] result = new byte[sha512.length + whirlPool.length];
		System.arraycopy(sha512, 0, result, 0, sha512.length);
		System.arraycopy(whirlPool, 0, result, sha512.length, whirlPool.length);
		return result;
	}

}
