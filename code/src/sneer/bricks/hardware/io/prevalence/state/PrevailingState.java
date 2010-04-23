package sneer.bricks.hardware.io.prevalence.state;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Producer;

@Brick
public interface PrevailingState {

	boolean isPrevailing();

	<T> T produce(Producer<T> producerThatDoesntEnterPrevalence, Producer<T> producerThatEntersPrevalence);

	<T> T produce(Producer<T> producerThatEntersPrevalence);

}
