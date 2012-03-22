package sneer.bricks.pulp.notifiers;

import basis.brickness.Brick;
import basis.lang.Producer;

@Brick
public interface Notifiers {
	
	<T> Notifier<T> newInstance();

	/** @param welcomer will be called every time a receiver is added to the returned sender, to produce a T to be sent to that receiver. */
	<T> Notifier<T> newInstance(Producer<? extends T> welcomer);

}
