package sneer.bricks.hardware.cpu.codec;

import sneer.foundation.brickness.Brick;

@Brick
public interface Codec {

	Hex hex() ;
	Base64 base64();

	interface Hex {
		String encode(byte[] bytes);
		byte[] decode(String hexString) throws DecodeException;
	}

	interface Base64 {
		String encode(final byte[] bytes);
		String encode(String text);
	}

}
