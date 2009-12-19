package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.brickness.Seal;
import sneer.foundation.brickness.Tuple;

public class OldFileContents extends Tuple {

	public final ImmutableByteArray bytes;
	public final String debugInfo;

	public OldFileContents(Seal adressee_, ImmutableByteArray bytes_, String debugInfo_) {
		super(adressee_);
		bytes = bytes_;
		debugInfo = debugInfo_;
	}

}
