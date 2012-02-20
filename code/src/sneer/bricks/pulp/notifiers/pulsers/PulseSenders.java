package sneer.bricks.pulp.notifiers.pulsers;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.brickness.Brick;

@Brick
public interface PulseSenders {
	
	PulseSender newInstance();

	WeakContract receive(Runnable receiver, Pulser... multipleSources);

}
