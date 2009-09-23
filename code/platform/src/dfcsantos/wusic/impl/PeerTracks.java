package dfcsantos.wusic.impl;

import dfcsantos.wusic.Track;

class PeerTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new PeerTracks();

	private PeerTracks() {};
	
	@Override
	Track nextTrack() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	void noWay() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	void meToo() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
