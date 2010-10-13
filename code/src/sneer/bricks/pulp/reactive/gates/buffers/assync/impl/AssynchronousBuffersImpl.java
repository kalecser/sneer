package sneer.bricks.pulp.reactive.gates.buffers.assync.impl;

import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.gates.buffers.assync.AssynchronousBuffers;

class AssynchronousBuffersImpl implements AssynchronousBuffers {

	@Override
	public <T> EventSource<T> createFor(EventSource<T> input, String threadName) {
		return new AssynchronousBufferImpl<T>(input, threadName).output();
	}
	
}
