package sneer.bricks.hardware.cpu.crypto.impl;

import static basis.environments.Environments.my;
import static org.bouncycastle.jce.provider.asymmetric.ec.ECUtil.generatePrivateKeyParameter;
import static org.bouncycastle.jce.provider.asymmetric.ec.ECUtil.generatePublicKeyParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;

import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.jce.provider.JDKKeyFactory;
import org.bouncycastle.jce.provider.JDKMessageDigest;
import org.bouncycastle.jce.provider.asymmetric.ec.KeyPairGenerator;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Digester;
import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import basis.lang.ProducerX;
import basis.lang.arrays.ImmutableByteArray;

class CryptoImpl implements Crypto {

	private static final int FILE_BLOCK_SIZE = 1024 * 100;
	
	@Override
	public Hash digest(byte[] input) {
		return digest(input, new JDKMessageDigest.SHA512());
	}
	
	
	private Hash digest(byte[] input, MessageDigest messageDigest) {
		return newDigester(messageDigest).digest(input);
	}


	@Override
	public Digester newDigester() {
		return newDigester(new JDKMessageDigest.SHA512());
	}


	private Digester newDigester(MessageDigest messageDigest) {
		return new DigesterImpl(messageDigest);
	}


	@Override
	public Hash digest(File file) throws IOException {
		if (file.isDirectory()) throw new IllegalArgumentException("The parameter cannot be a directory");

		Digester digester = newDigester();
		FileInputStream input = new FileInputStream(file);
		try {
			byte[] block = new byte[FILE_BLOCK_SIZE];
			for (int numOfBytes = input.read(block); numOfBytes != -1; numOfBytes = input.read(block)) {
				my(CpuThrottle.class).yield();
				digester.update(block, 0, numOfBytes);
			}
		} finally {
			try { input.close(); } catch (Throwable ignore) {}
		}

		return digester.digest();
	}
	

	@Override
	public Hash digest(Path file) throws IOException {
		if (Files.isDirectory(file)) throw new IllegalArgumentException("The parameter cannot be a directory");

		Digester digester = newDigester();
		try (InputStream input = Files.newInputStream(file)) {
			byte[] block = new byte[FILE_BLOCK_SIZE];
			for (int numOfBytes = input.read(block); numOfBytes != -1; numOfBytes = input.read(block)) {
				my(CpuThrottle.class).yield();
				digester.update(block, 0, numOfBytes);
			}
		} 

		return digester.digest();
	}
	

	@Override
	public Hash unmarshallHash(byte[] bytes) {
		return new Hash(new ImmutableByteArray(bytes));
	}

	
	@Override
	public Signature getSHA512WithECDSA() {
		try {
			return Signature.getInstance("SHA512WITHECDSA", "BC"); //Use concrete class instead of provider
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	
	@Override
	public KeyPair newECDSAKeyPair(final byte[] seed) {
		KeyPairGenerator.EC generator = new KeyPairGenerator.ECDSA();
		generator.initialize(256, new RandomWrapper(mix256bits(seed)));
		return generator.generateKeyPair();
	}
		

	private byte[] mix256bits(final byte[] seed) {
		byte[] sha512Hash = digest(seed).bytes.copy();
		return Arrays.copyOf(sha512Hash, 32); //32 * 8 = 256
	}
	
	
	@Override
	public ECBCipher newAES256Cipher(byte[] key) {
		return new ECBCipherImpl(key);
	}
	

	@Override
	public PublicKey unmarshalPublicKey(final byte[] keyBytes) {
		return safelyProduce(new ProducerX<PublicKey, Exception>() { @Override public PublicKey produce() throws IOException {
			return JDKKeyFactory.createPublicKeyFromDERStream(keyBytes);
		}});
	}
	

	@Override
	public Hash secretKeyFrom(final PublicKey publicKey, final PrivateKey privateKey) {
		return safelyProduce(new ProducerX<Hash, Exception>() { @Override public Hash produce() throws InvalidKeyException {
			ECDHBasicAgreement agreement = new ECDHBasicAgreement();
			agreement.init(generatePrivateKeyParameter(privateKey));
			BigInteger ret = agreement.calculateAgreement(generatePublicKeyParameter(publicKey));
			
			return digest(ret.toByteArray(), new JDKMessageDigest.SHA256());
		}});
	}
	
	
	private <T> T safelyProduce(ProducerX<T, Exception> producer) {
		try {
			return producer.produce();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

}
