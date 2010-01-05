package sneer.bricks.pulp.events.pulsers;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

/** Produces "pulses" at particularly interesting moments.
 *  IMPORTANT: All PulseSources (Signal for example) can only be publicly OBSERVABLE and not MUTABLE. They are mutated via some other controlling object (Register for example). */
public interface PulseSource {
	
	WeakContract addPulseReceiver(Runnable pulseReceiver);

}
