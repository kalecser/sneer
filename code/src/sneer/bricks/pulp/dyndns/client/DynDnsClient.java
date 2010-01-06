package sneer.bricks.pulp.dyndns.client;

import sneer.foundation.brickness.Brick;

@Brick
public interface DynDnsClient {
	
	//Fix: Running on more than one machine will cause redundant (abusive) updates.
	// Stop persisting last discovered IP. Do a DNS lookup every time instead.
	void dummyMethodSoThisSnappIsntStarted();
	
}
