package dfcsantos.wusic.impl;

import dfcsantos.wusic.Track;


abstract class TrackSourceStrategy {

//	Super
//		Manage Playlist
//			Use root folder.
//			Ignore "deleted" and "candidates" subfolders.
//	
//		No Way:
//			record bad hash
//			delete
//	
//		shuffle
//
//	
//	OwnTracks
//		root folder: Chosen subfolder of (OwnTracksFolderKeeper.class).ownTracksFolder()
//
//		No Way:
//			copy to "deleted"
//			super.NoWay()
//
//
//	PeerTracks
//		root folder: (OwnTracksFolderKeeper.class).ownTracksFolder() / candidates
//
//		Me Too
//			Move from candidates to (OwnTracksFolderKeeper.class).ownTracksFolder()
//
	
	
	
	@SuppressWarnings("unused")	private boolean _isShuffleOn;

	
	abstract Track nextTrack(); //Implement here calling abstract listFiles().

	abstract void noWay(Track rejected);

	
	void setShuffleMode(boolean shuffle) {
		_isShuffleOn = shuffle;
	}

}
