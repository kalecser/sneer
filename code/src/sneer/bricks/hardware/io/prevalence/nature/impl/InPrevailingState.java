package sneer.bricks.hardware.io.prevalence.nature.impl;

import sneer.foundation.lang.Producer;


public class InPrevailingState {
	
	static boolean _prevailing;
	
	public static synchronized <T> T produce(final Producer<T> producerThatDoesntEnterPrevalence, Producer<T> producerThatEntersPrevalence) {
		
		if (_prevailing)
			return producerThatDoesntEnterPrevalence.produce();
		
		_prevailing = true;
		try {
			return producerThatEntersPrevalence.produce();
		} finally {
			_prevailing = false;
		}
	}

	public static <T> T produce(final Producer<T> producer) {
		return produce(producer, producer);
	}
	
}
