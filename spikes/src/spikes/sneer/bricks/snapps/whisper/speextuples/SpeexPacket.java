package spikes.sneer.bricks.snapps.whisper.speextuples;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.ram.arrays.Immutable2DByteArray;

public class SpeexPacket extends Tuple {
	
	public final Immutable2DByteArray frames;
	public final String room;
	public final short sequence;

	public SpeexPacket(Immutable2DByteArray frames_, String room_, short sequence_) {
		frames = frames_;
		room = room_;
		sequence = sequence_;
	}

}