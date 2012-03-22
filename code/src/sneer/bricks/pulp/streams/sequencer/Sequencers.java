package sneer.bricks.pulp.streams.sequencer;

import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface Sequencers {

	<T> Sequencer<T> createSequencerFor(short bufferSize,short maxGap, Consumer<T> consumer);
	
}
