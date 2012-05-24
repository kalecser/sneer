package sneer.bricks.snapps.games.go.impl.gui;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;


public interface TimerFactory {

	WeakContract wakeUpEvery(int interval, Runnable scroller);

}
