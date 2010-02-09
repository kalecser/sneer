package sneer.bricks.pulp.keymanager;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.lang.Immutable;

public class Seal extends Immutable {

	public final ImmutableByteArray bytes;

	public Seal(ImmutableByteArray bytes_) {
		bytes = bytes_;
	}

	public String toFormattedHexString() {
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
		String hexString = my(Codec.class).hex().encode(bytes.copy()).toUpperCase();
		return " " + my(Lang.class).strings().insertSpacedSeparators(
			my(Lang.class).strings().insertSpacedSeparators(hexString, " ", 4), "\n ", 40
		);
	}

}
