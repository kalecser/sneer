package sneer.bricks.hardware.cpu.codec.impl;

import java.nio.charset.Charset;

import org.apache.commons.codec.DecoderException;

import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;

class CodecImpl implements Codec {

	static private Charset UTF8 = Charset.forName("UTF-8");
	
	private final Hex _hex = new Codec.Hex() {
		@Override public String encode(byte[] bytes) { return org.apache.commons.codec.binary.Hex.encodeHexString(bytes); }

		@Override
		public byte[] decode(String hexString) throws DecodeException {
			try {
				return org.apache.commons.codec.binary.Hex.decodeHex(hexString.toCharArray());
			} catch (DecoderException e) {
				throw new DecodeException(e);
			}
		}		
	};

	private final Base64 _base64 = new Codec.Base64() {
		@Override public String encode(String string) { return encode(string.getBytes(UTF8)); }
		@Override public String encode(final byte[] bytes) { return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes); }
	};

	@Override public Base64 base64() { return _base64; }
	@Override public Hex hex() { return _hex; }

}
