package sneer.bricks.identity.seals.codec.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.Seal;

class SealCodec implements sneer.bricks.identity.seals.codec.SealCodec {

	@Override
	public String hexEncode(Seal seal) {
		return my(Codec.class).hex().encode(seal.bytes.copy());
	}

	@Override
	public String formattedHexEncode(Seal seal) {
		/*	Format used (128 bytes represented in hexadecimal):
		 *
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 *	12AB 12AB 21AB 21F4 2E44 2A34 1C34 123F
		 * 
		 */
		return " " + my(Lang.class).strings().insertSpacedSeparators(
			my(Lang.class).strings().insertSpacedSeparators(hexEncode(seal), " ", 4), "\n ", 40
		);
	}

	@Override
	public Seal hexDecode(String seal) throws DecodeException {
		return new Seal(new ImmutableByteArray(my(Codec.class).hex().decode(seal)));		
	}

}
