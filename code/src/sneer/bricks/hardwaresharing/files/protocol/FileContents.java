package sneer.bricks.hardwaresharing.files.protocol;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.brickness.Seal;
import sneer.foundation.brickness.Tuple;

public class FileContents extends Tuple {

	public final ImmutableByteArray bytes;

	public FileContents(Seal adressee_, ImmutableByteArray bytes_) {
		super(adressee_);
		bytes = bytes_;
	}

}
