package sneer.bricks.hardware.io.prevalence.state;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Producer;

@Brick
public interface PrevailingState {

	boolean isPrevailing();

	<T> T produce(Producer<T> producerToEnterPrevalence, Producer<T> producerToRunInsidePrevalence);

	<T> T produce(Producer<T> producerToEnterPrevalence);

}
