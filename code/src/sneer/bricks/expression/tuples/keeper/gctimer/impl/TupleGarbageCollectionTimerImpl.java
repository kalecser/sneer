package sneer.bricks.expression.tuples.keeper.gctimer.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.keeper.TupleKeeper;
import sneer.bricks.expression.tuples.keeper.gctimer.TupleGarbageCollectionTimer;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.bricks.snapploader.SnappLoader;

public class TupleGarbageCollectionTimerImpl implements TupleGarbageCollectionTimer {

	private static final SnappLoader SnappLoader = my(SnappLoader.class);
	private static final int TEN_MINUTES = 1000 * 60 * 10;

	@SuppressWarnings("unused") private WeakContract _refToAvoidGc;
	
	
	{
		_refToAvoidGc = my(Timer.class).wakeUpEvery(TEN_MINUTES, new Runnable() {  @Override public void run() {
			SnappLoader.loadingFinished().waitTillOpen();
			if (SnappLoader.wereThrowablesCaughtWhenLoadingSnapps())
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Old Tuples will not be deleted.", "There was a problem (throwable thrown) while loading some Snapp. Old tuples will not be deleted because there is no way to know what tuples are needed by the broken Snapp.", TEN_MINUTES);
			else
				my(TupleKeeper.class).garbageCollect();
		}});
	}
	
}
