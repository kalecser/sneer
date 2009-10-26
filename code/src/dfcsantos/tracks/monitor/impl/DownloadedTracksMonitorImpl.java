package dfcsantos.tracks.monitor.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.monitor.DownloadedTracksMonitor;

class DownloadedTracksMonitorImpl implements DownloadedTracksMonitor {

	@Override
	public boolean isTrackAlreadyDownloaded(Track trackToCheck) {
		return isTrackInTheFolder(trackToCheck, my(TracksFolderKeeper.class).sharedTracksFolder().currentValue())
			|| isTrackInTheFolder(trackToCheck, my(TracksFolderKeeper.class).peerTracksFolder());
	}

	private boolean isTrackInTheFolder(Track track, File folder) {
		return my(Tracks.class).listMp3FilesFromFolder(folder).contains(track.file());
	}

}
