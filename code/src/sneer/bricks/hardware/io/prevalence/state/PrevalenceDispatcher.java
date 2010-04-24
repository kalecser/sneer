package sneer.bricks.hardware.io.prevalence.state;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Producer;

//This brick implements the strategy for a dispatcher OUTSIDE the prevalence environment. Inside the environment different implementation is bound. See implementing classes.   
@Brick
public interface PrevalenceDispatcher {

	void checkInsidePrevalence();

	<T> T produce(Producer<T> producerForOutsidePrevalence, Producer<T> producerForInsidePrevalence);

}
