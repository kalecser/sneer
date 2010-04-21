package spikes.sneer.bricks.snapps.whisper.speextuples;

import sneer.bricks.expression.tuples.Tuple;
import sneer.foundation.lang.arrays.ImmutableByteArray2D;

public class SpeexPacket extends Tuple {
	
	public final ImmutableByteArray2D frames;
	public final String room;
	public final short sequence;

	public SpeexPacket(ImmutableByteArray2D frames_, String room_, short sequence_) {
		frames = frames_;
		room = room_;
		sequence = sequence_;
	}

}