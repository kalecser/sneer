package sneer.bricks.hardware.cpu.codecs.crypto;

import sneer.foundation.lang.ReadOnly;

public interface Sneer1024 extends ReadOnly {

	byte[] bytes();
	
	String toHexa();

}
