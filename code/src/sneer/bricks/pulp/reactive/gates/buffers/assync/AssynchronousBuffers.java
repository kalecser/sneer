package sneer.bricks.pulp.reactive.gates.buffers.assync;

import sneer.bricks.pulp.notifiers.Source;
import sneer.foundation.brickness.Brick;


@Brick
public interface AssynchronousBuffers {

	<T> Source<T> createFor(Source<T> input, String threadName);

}
