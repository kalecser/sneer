package sneer.bricks.pulp.reactive.signalchooser;

import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface SignalChoosers {
	
	//Refactor: change return from Object to WeakContract.
	<T> Object receive(CollectionSignal<T> input, ListOfSignalsReceiver<T> listOfSignalsReceiver);
}
