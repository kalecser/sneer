package sneer.bricks.pulp.streams.sequencer.impl;

import basis.lang.Consumer;
import sneer.bricks.pulp.streams.sequencer.Sequencer;
import sneer.bricks.pulp.streams.sequencer.Sequencers;

class SequencersImpl implements Sequencers {

	@Override
	public <T> Sequencer<T> createSequencerFor(short bufferSize, short maxGap, Consumer<T> consumer) {
		return new SequencerImpl<T>(consumer, bufferSize, maxGap);
	}

}