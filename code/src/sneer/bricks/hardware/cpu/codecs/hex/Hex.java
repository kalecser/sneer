package sneer.bricks.hardware.cpu.codecs.hex;

import sneer.foundation.brickness.Brick;

@Brick
public interface Hex {

	String encode(byte[] bytes);

	byte[] decode(String hexString) throws DecodeException;

}
