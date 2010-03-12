package sneer.bricks.identity.seals.codec;

import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface SealCodec {

	String hexEncode(Seal seal);
	String formattedHexEncode(Seal seal);

	Seal hexDecode(String seal) throws DecodeException;

}
