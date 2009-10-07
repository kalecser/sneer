package dfcsantos.wusic.impl;

class PeerTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new PeerTracks();

	private PeerTracks() {};

	@Override
	String tracksSubfolder() {
		return "candidates";
	}

	void meToo() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
