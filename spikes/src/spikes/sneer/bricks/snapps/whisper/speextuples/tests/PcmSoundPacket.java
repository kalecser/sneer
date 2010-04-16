package spikes.sneer.bricks.snapps.whisper.speextuples.tests;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;

//This class was deleted. It is here just to make the test compile.
class PcmSoundPacket extends Tuple {

	public final ImmutableByteArray payload;
	
	public PcmSoundPacket(ImmutableByteArray payload_) {
		payload = payload_;
	}

}
