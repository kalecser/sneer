package sneer.bricks.pulp.reactive.collections.setfilter;

import basis.brickness.Brick;
import basis.lang.Predicate;
import sneer.bricks.pulp.reactive.ReactivePredicate;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface SetFilter {

	<T> SetSignal<T> filter(SetSignal<T> input, Predicate<T> predicate);
	<T> SetSignal<T> filter(SetSignal<T> input, ReactivePredicate<T> predicate);
}