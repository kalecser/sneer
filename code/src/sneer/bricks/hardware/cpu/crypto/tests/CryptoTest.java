package sneer.bricks.hardware.cpu.crypto.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class CryptoTest extends BrickTest { 

	/** See http://en.wikipedia.org/wiki/SHA1 and http://en.wikipedia.org/wiki/WHIRLPOOL */
	private static final String SHA512    = "07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6";
	private static final String WHIRLPOOL = "b97de512e91e3828b40d2b0fdce9ceb3c4a71f9bea8d88e75c4fa854df36725fd2b52eb6544edcacd6f8beddfea403cb55ae31f03ad62a5ef54e42ee82c3fb35";

	private final Crypto _subject = my(Crypto.class);

	@Test
	public void testSneer1024WithSmallString() throws Exception {
		final String INPUT = "The quick brown fox jumps over the lazy dog";

		Sneer1024 hashOfString = _subject.digest(INPUT.getBytes());
		assertEquals(1024, hashOfString.bytes().length * 8);
		assertHexa(SHA512 + WHIRLPOOL, hashOfString.bytes());

		File file = createFileWithContent(INPUT.getBytes());
		Sneer1024 hashOfFile = _subject.digest(file);
		assertEquals(hashOfString, hashOfFile);
	}

	@Test
	public void testSneer1024WithLargeArray() throws Exception {
		final byte[] INPUT = new byte[30720]; // 20 KB
		new Random().nextBytes(INPUT);
		Sneer1024 hashOfArray = _subject.digest(INPUT);

		File file = createFileWithContent(INPUT);
		Sneer1024 hashOfFile = _subject.digest(file);
		assertEquals(hashOfArray, hashOfFile);
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

}
