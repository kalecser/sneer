package dfcsantos.tracks.assessment.assessor.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

abstract class AbstractTrackAssessment implements TrackAssessment {

	private final Track _track;
	private final Contact _trackSource;

	AbstractTrackAssessment(Track toBeAssessed) {
		_track = toBeAssessed;
		_trackSource = sourceOf(toBeAssessed);
		my(Logger.class).log(this.toString());
	}

	@Override
	public Track track() {
		return _track;
	}

	@Override
	public Contact trackSource() {
		return _trackSource;
	}

	String trackSourceName() {
		return (_trackSource == null) ? "unidentified" : _trackSource.nickname().currentValue();
	}

	@Override
	public String toString() {
		return "Track Assessment: " + _track.name();
	}

	private Contact sourceOf(Track track) {
		File tracksFolder = track.file().getParentFile();
		if (sharedTracksFolder().equals(tracksFolder)) return null;
		return my(Contacts.class).contactGiven(tracksFolder.getName());
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

}
