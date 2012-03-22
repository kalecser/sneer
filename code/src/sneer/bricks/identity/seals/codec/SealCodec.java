package sneer.bricks.identity.seals.codec;

import basis.brickness.Brick;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.identity.seals.Seal;

@Brick
public interface SealCodec {

	String hexEncode(Seal seal);
	String formattedHexEncode(Seal seal);

	Seal hexDecode(String seal) throws DecodeException;

}
