package sneer.bricks.pulp.reactive.gates.buffers.assync;

import basis.brickness.Brick;
import sneer.bricks.pulp.notifiers.Source;


@Brick
public interface AssynchronousBuffers {

	<T> Source<T> createFor(Source<T> input, String threadName);

}
