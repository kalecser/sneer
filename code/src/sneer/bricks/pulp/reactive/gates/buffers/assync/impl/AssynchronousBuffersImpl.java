package sneer.bricks.pulp.reactive.gates.buffers.assync.impl;

import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.pulp.reactive.gates.buffers.assync.AssynchronousBuffers;

class AssynchronousBuffersImpl implements AssynchronousBuffers {

	@Override
	public <T> Source<T> createFor(Source<T> input, String threadName) {
		return new AssynchronousBufferImpl<T>(input, threadName).output();
	}
	
}
