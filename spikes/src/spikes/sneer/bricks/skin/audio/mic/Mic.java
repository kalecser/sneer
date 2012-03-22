package spikes.sneer.bricks.skin.audio.mic;

import basis.brickness.Brick;
import basis.lang.arrays.ImmutableByteArray;
import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface Mic {

	/** Will start producing sound packets with duration of one hundredth of a second each. See sound() method. */
	void open();
	void close();
	Signal<Boolean> isOpen();
	
	/** Produces packets of one hundreth of a second of PCM-encoded sound: 8000Hz, 16 bits, 2 Channels (Stereo), Signed, Little Endian */
	Source<ImmutableByteArray> sound();
	
}
