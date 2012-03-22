package sneer.bricks.pulp.notifiers.pulsers;

import basis.lang.ReadOnly;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

/** Produces "pulses" at particularly interesting moments. */
public interface Pulser extends ReadOnly {
	
	WeakContract addPulseReceiver(Runnable pulseReceiver);

}
