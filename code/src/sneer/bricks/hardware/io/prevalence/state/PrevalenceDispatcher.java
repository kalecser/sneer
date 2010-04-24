package sneer.bricks.hardware.io.prevalence.state;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Producer;

@Brick
public interface PrevalenceDispatcher {

	boolean isPrevailing();

	<T> T produce(Producer<T> producerForOutsidePrevalence, Producer<T> producerForInsidePrevalence);

	<T> T produceOutsidePrevalence(Producer<T> producer);

}
