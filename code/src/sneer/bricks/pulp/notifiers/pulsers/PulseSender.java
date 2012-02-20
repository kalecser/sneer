package sneer.bricks.pulp.notifiers.pulsers;


public interface PulseSender {

	Pulser output();
	
	/** Sends a pulse to all receivers of output() (runs them). */
	void sendPulse();
	
}
