package sneer.bricks.hardware.cpu.codecs.hex.impl;

import org.apache.commons.codec.DecoderException;

import sneer.bricks.hardware.cpu.codecs.hex.DecodeException;
import sneer.bricks.hardware.cpu.codecs.hex.Hex;

class HexImpl implements Hex {

	@Override
	public String encode(byte[] bytes) {
		return org.apache.commons.codec.binary.Hex.encodeHexString(bytes); 
	}

	@Override
	public byte[] decode(String hexString) throws DecodeException {
		try {
			return org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
		} catch (DecoderException e) {
			throw new DecodeException(e);
		}
	}

}
