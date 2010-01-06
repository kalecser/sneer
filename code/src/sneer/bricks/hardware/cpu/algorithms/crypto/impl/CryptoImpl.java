package sneer.bricks.hardware.cpu.algorithms.crypto.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Security;
import java.util.concurrent.atomic.AtomicInteger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Digester;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.ByRef;

class CryptoImpl implements Crypto {

	private static final int FILE_BLOCK_SIZE = 102400;

	static {
		Security.addProvider(new BouncyCastleProvider()); //Optimize: remove this static dependency. Use Bouncycastle classes directly
	}

	@Override
	public Sneer1024 digest(byte[] input) {
		return newDigester().digest(input);
	}

	@Override
	public Digester newDigester() {
		return new DigesterImpl(messageDigest("SHA-512", "SUN"), messageDigest("WHIRLPOOL", "BC"));
	}

	private MessageDigest messageDigest(String algorithm, String provider) {
		try {
			return MessageDigest.getInstance(algorithm, provider);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Sneer1024 digest(final File file) throws IOException {
		if (file.isDirectory()) throw new IllegalArgumentException("The parameter cannot be a directory");

		final ByRef<Digester> digester = ByRef.newInstance(newDigester());
		final Latch digesterUpdate = my(Latches.class).produce();
		final int numberOfBlocks = my(IO.class).files().fileSizeInBlocks(file.length(), FILE_BLOCK_SIZE);
		final AtomicInteger blockNumber = new AtomicInteger(0);

		my(Threads.class).startStepping(new Runnable() { @Override public void run() {
			if (blockNumber.get() == numberOfBlocks) {
				digesterUpdate.open();
				return;
			}
			try {
				byte[] block = my(IO.class).files().readBlock(file, blockNumber.getAndIncrement(), FILE_BLOCK_SIZE);
				digester.value.update(block, 0, block.length);
			} catch (IOException ioe) {
				digester.value = null;
				my(Logger.class).log("Error reading file: ", file);
				digesterUpdate.open();
			}
		}});

		digesterUpdate.waitTillOpen();
		if (digester.value == null)
			throw new IOException("Error computing hash of file: " + file);
		return digester.value.digest();
	}

	@Override
	public Sneer1024 unmarshallSneer1024(byte[] bytes) {
		return new Sneer1024Impl(bytes);
	}

	@Override
	public String toHexa(byte[] bytes) {
		return new String(Hex.encode(bytes));
	}

}
