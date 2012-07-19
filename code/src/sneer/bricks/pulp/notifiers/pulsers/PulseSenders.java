package sneer.bricks.pulp.notifiers.pulsers;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import basis.brickness.Brick;
import basis.lang.Closure;

@Brick
public interface PulseSenders {
	
	PulseSender newInstance();

	WeakContract receive(Closure receiver, Pulser... multipleSources);

}
