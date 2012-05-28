package sneer.bricks.snapps.games.go.impl;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;


public interface TimerFactory {

	WeakContract wakeUpEvery(int interval, Runnable scroller);

}
