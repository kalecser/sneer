package sneer.bricks.pulp.notifiers.pulsers;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.ReadOnly;

/** Produces "pulses" at particularly interesting moments. */
public interface Pulser extends ReadOnly {
	
	WeakContract addPulseReceiver(Runnable pulseReceiver);

}
