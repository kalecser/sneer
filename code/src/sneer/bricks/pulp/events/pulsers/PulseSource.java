package sneer.bricks.pulp.events.pulsers;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.ReadOnly;

/** Produces "pulses" at particularly interesting moments. */
public interface PulseSource extends ReadOnly {
	
	WeakContract addPulseReceiver(Runnable pulseReceiver);

}
