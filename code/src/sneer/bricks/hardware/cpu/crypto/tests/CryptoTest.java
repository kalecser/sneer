package sneer.bricks.hardware.cpu.crypto.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class CryptoTest extends BrickTestBase {
	
	/** See http://en.wikipedia.org/wiki/SHA1 and http://en.wikipedia.org/wiki/WHIRLPOOL */
	private static final String SHA512 = "07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6";
	static private Charset UTF8 = Charset.forName("UTF-8");
	

	private final Crypto subject = my(Crypto.class);

	
	@Test
	public void testDigestWithSmallString() throws Exception {
		final String INPUT = "The quick brown fox jumps over the lazy dog";

		Hash hashOfString = subject.digest(INPUT.getBytes(UTF8));
		assertEquals(512, hashOfString.bytes.copy().length * 8);
		assertHexa(SHA512, hashOfString.bytes.copy());

		File file = createFileWithContent(INPUT.getBytes(UTF8));
		Hash hashOfFile = subject.digest(file);
		assertEquals(hashOfString, hashOfFile);
	}

	
	@Test
	public void testDigestWithLargeArray() throws Exception {
		final byte[] INPUT = new byte[30720];
		new Random().nextBytes(INPUT);
		Hash hashOfArray = subject.digest(INPUT);

		File file = createFileWithContent(INPUT);
		Hash hashOfFile = subject.digest(file);
		assertEquals(hashOfArray, hashOfFile);
	}
	
	
	@Test
	public void testECBCipher() {
		ECBCipher cipher = subject.newAES256Cipher(new byte[32], new byte[32]);
		assertECB(cipher, "Hey Neide!");
		
		assertECB(cipher, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam in lorem sapien. Ut interdum porta odio, " +
				"eget ultrices dolor adipiscing non. Sed a tellus nulla. Sed ac eros eget nisl sodales vehicula. " +
				"Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. " +
				"Ut vel nunc purus, eu elementum dolor. Sed diam urna, tincidunt sed sagittis nec, luctus eleifend ante. " +
				"Nulla facilisi. Praesent vitae turpis nibh, sed pharetra nisi. Nullam ac urna in sapien aliquam convallis eu quis orci. " +
				"In in eros ultrices eros gravida dictum. Morbi facilisis lorem id dui accumsan a dapibus justo vulputate.");
	}
	
	
	@Test
	public void AESTestVector() throws DecodeException {
		byte[] key = fromHex("c47b0294dbbbee0fec4757f22ffeee3587ca4730c3d33b691df38bab076bc558");
		byte[] plainText = fromHex("00000000000000000000000000000000");
		byte[] cipherText = fromHex("46f2fb342d6f0ab477476fc501242c5f"); // from NIST
		
		ECBCipher cipher = subject.newAES256Cipher(key, key);
		byte[] actualWithoutPadding = Arrays.copyOf(cipher.encrypt(plainText), 16);
		
		assertArrayEquals(cipherText, actualWithoutPadding);
	}
	
	
	@Test
	public void unmarshalPublicKeyFromKeyBytes() throws DecodeException {
		byte[] ecdsaPublicKey = fromHex("3059301306072a8648ce3d020106082a8648ce3d03010703420004d647ab7e67b1e0f58aece6d386c5fb8fc8c16e2566539678df82984c8c642c60bbda8f6abed26f279d13858613ff83cc80d9cb95e0dd261dcc7e12f1ffe2a922");
		PublicKey publicKey = subject.unmarshalPublicKey(ecdsaPublicKey);
		assertArrayEquals(ecdsaPublicKey, publicKey.getEncoded());
	}
	
	
	@Test
	public void ECDHSecret() throws DecodeException {
		KeyPair pair1 = subject.newECDSAKeyPair("seed 1".getBytes(UTF8));
		KeyPair pair2 = subject.newECDSAKeyPair("seed 2".getBytes(UTF8));
		byte[] sessionKey = new BigInteger("1000000").toByteArray();
		
		Hash secretFromPair1 = subject.secretKeyFrom(pair1.getPublic(), pair2.getPrivate(), sessionKey);
		assertArrayEquals(fromHex("5a8fe80f4ec554e3876fb3271594b6d046ed22dca7770c730f633183007119d1543730c53cfa7ac03326fa5f2379d4b92aaa8d7a9c80fb309a2ecc8401b93ffd"), secretFromPair1.bytes.copy());
		
		Hash secretFromPair2 = subject.secretKeyFrom(pair2.getPublic(), pair1.getPrivate(), sessionKey);
		assertEquals(secretFromPair2, secretFromPair1);
		
		Hash secretWithAnotherSessionKey = subject.secretKeyFrom(pair1.getPublic(), pair2.getPrivate(), new BigInteger("2000000").toByteArray());
		assertArrayEquals(fromHex("654a6a3e0d5b9e0253893a2e8fd7d972bd5e76d8a588ea3d6e7fb8aafb41a1b61ea438f802c6d94f851ee03df61d0e1649b4307b343ba8514c127706299f5371"), secretWithAnotherSessionKey.bytes.copy());
	}
	
	
	private byte[] fromHex(String hexString) throws DecodeException {
		return my(Codec.class).hex().decode(hexString);
	}


	private File createFileWithContent(byte[] content) throws IOException {
		File result = newTmpFile();
		FileOutputStream output = new FileOutputStream(result);
		output.write(content);
		output.close();

		return result;
	}

	
	private void assertHexa(String expected, byte[] hash) {
		assertEquals(expected, my(Codec.class).hex().encode(hash));
	}
	
	
	private void assertECB(ECBCipher cipher, String message) {
		byte[] cipherText = cipher.encrypt(message.getBytes(UTF8));
		byte[] plainText = cipher.decrypt(cipherText);
		
		assertEquals(message, new String(plainText, UTF8));
	}

}
