package sneer.bricks.pulp.reactive;

import basis.brickness.Brick;
import basis.lang.Predicate;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface SignalUtils {

	<T> void waitForValue(Signal<T> signal, T expectedValue);

	<T> void waitForElement(SetSignal<T> setSignal, T expected);

	<T> void waitForElement(SetSignal<T> setSignal,	Predicate<T> predicate);

}