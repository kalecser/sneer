package snype.whisper.speextuples;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;
import basis.lang.arrays.ImmutableByteArray2D;

public class SpeexPacket extends Tuple {
	
	public final ImmutableByteArray2D frames;
	public final String room;
	public final short sequence;

	public SpeexPacket(ImmutableByteArray2D frames_, String room_, short sequence_) {
		frames = frames_;
		room = room_;
		sequence = sequence_;
	}
	public SpeexPacket(Seal to_, ImmutableByteArray2D frames_, String room_, short sequence_) {
		super(to_);
		frames = frames_;
		room = room_;
		sequence = sequence_;
	}
	

}