package sneer.bricks.pulp.reactive.gates.buffers.assync;

import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.brickness.Brick;


@Brick
public interface AssynchronousBuffers {

	<T> EventSource<T> createFor(EventSource<T> input, String threadName);

}
