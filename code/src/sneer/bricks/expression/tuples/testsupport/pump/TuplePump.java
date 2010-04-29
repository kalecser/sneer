package sneer.bricks.expression.tuples.testsupport.pump;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

public interface TuplePump extends WeakContract {

	void waitForAllDispatchingToFinish();

}
