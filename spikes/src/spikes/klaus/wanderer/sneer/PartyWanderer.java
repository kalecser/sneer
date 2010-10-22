package spikes.klaus.wanderer.sneer;

import sneer.tests.SovereignParty;

public class PartyWanderer {

	private final SovereignParty _delegate;
	private int _shoutCount = 0;

	PartyWanderer(SovereignParty party) {
		_delegate = party;
	}

	void wander() {
		_delegate.shout("Shout from " + _delegate.ownName() + " " + ++_shoutCount);
	}

	SovereignParty delegate() {
		return _delegate;
	}

}
