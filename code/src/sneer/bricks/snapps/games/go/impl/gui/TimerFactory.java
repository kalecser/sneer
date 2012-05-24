package sneer.bricks.snapps.games.go.impl.gui;


public interface TimerFactory {

	void wakeUpEvery(int interval, Runnable scroller);

}
