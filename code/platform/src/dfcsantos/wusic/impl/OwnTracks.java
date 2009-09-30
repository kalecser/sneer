package dfcsantos.wusic.impl;


public class OwnTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new OwnTracks();

	private OwnTracks() {}

	@Override
	String tracksSubfolder() {
		return "";
	}

}
