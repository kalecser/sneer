package sneer.bricks.pulp.notifiers.pulsers;

import basis.brickness.Brick;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

@Brick
public interface PulseSenders {
	
	PulseSender newInstance();

	WeakContract receive(Runnable receiver, Pulser... multipleSources);

}
