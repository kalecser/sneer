package sneer.bricks.hardware.cpu.crypto;

import sneer.foundation.lang.ReadOnly;

public interface Sneer1024 extends ReadOnly {

	byte[] bytes();
	
	String toHexa();

}
