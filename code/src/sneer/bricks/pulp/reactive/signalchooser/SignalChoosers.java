package sneer.bricks.pulp.reactive.signalchooser;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;

@Brick
public interface SignalChoosers {
	
	//Refactor: change return from Object to WeakContract.
	<T> Object receive(CollectionSignal<T> input, ListOfSignalsReceiver<T> listOfSignalsReceiver);
}
