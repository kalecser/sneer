package sneer.bricks.hardware.cpu.algorithms.crypto.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class CryptoTest extends BrickTest {

	private static final String INPUT = "The quick brown fox jumps over the lazy dog"; 
	
	/** See http://en.wikipedia.org/wiki/SHA1 and http://en.wikipedia.org/wiki/WHIRLPOOL */
	private static final String SHA512    = "07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6";
	private static final String WHIRLPOOL = "b97de512e91e3828b40d2b0fdce9ceb3c4a71f9bea8d88e75c4fa854df36725fd2b52eb6544edcacd6f8beddfea403cb55ae31f03ad62a5ef54e42ee82c3fb35";

	
	private final Crypto _subject = my(Crypto.class);
	
	
	@Test
	public void testSneer1024() throws Exception {
		byte[] hash = _subject.digest(INPUT.getBytes()).bytes();
		assertEquals(1024, hash.length * 8);
		assertHexa(SHA512 + WHIRLPOOL, hash);
	}

	
	private void assertHexa(String expected, byte[] hash) {
		assertEquals(expected, _subject.toHexa(hash));
	}
}
