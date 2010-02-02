package dfcsantos.tracks.sharing.endorsements.client.downloads.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.expression.files.client.Download;
import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.sharing.endorsements.client.downloads.TrackDownloader;
import dfcsantos.tracks.sharing.endorsements.protocol.TrackEndorsement;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;
import dfcsantos.wusic.Wusic;

class TrackDownloaderImpl implements TrackDownloader {

	private final List<Download> _downloads = Collections.synchronizedList(new ArrayList<Download>());
	private final Register<Integer> _numberOfDownloadedTracks = my(Signals.class).newRegister(0);

	@SuppressWarnings("unused") private final WeakContract _trackEndorsementConsumerContract;
	private boolean _isActive = false;

	{
		_trackEndorsementConsumerContract = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			consumeTrackEndorsement(trackEndorsement);
		}});
	}

	public Signal<Integer> numberOfDownloadedTracks() {
		return _numberOfDownloadedTracks.output();
	}

	@Override
	public void setActive(boolean isActive) {
		_isActive = isActive;
	}

	private void consumeTrackEndorsement(TrackEndorsement endorsement) {
		if (!_isActive ) return;

		if (!isTracksDownloadAllowed()) return;
		if (my(Seals.class).ownSeal().equals(endorsement.publisher)) return;
		if (isDuplicated(endorsement)) return;
		if (isRejected(endorsement)) return;

		if (_downloads.size() >= 3) return;
		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash);
		_downloads.add(download);

		my(Threads.class).startDaemon("Waiting for Download", new Closure() { @Override public void run() {
			try {
				download.waitTillFinished();
				_numberOfDownloadedTracks.setter().consume(_numberOfDownloadedTracks.output().currentValue() + 1);
			} catch (IOException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			} finally {
				_downloads.remove(download);
			}
		}});
	}

	private boolean isRejected(TrackEndorsement endorsement) {
		return my(RejectedTracksKeeper.class).isRejected(endorsement.hash);
	}

	private boolean isDuplicated(TrackEndorsement endorsement) {
		return my(FileMap.class).getFile(endorsement.hash) != null;
	}

	private boolean isTracksDownloadAllowed() {
		if (!my(Wusic.class).isTracksDownloadAllowed().currentValue()) return false;
		return peerTracksFolderSize() < downloadAllowanceInBytes();
	}

	private long peerTracksFolderSize() {
		return my(IO.class).files().folderSize(peerTracksFolder());
	}

	private int downloadAllowanceInBytes() {
		return 1024 * 1024 * my(Wusic.class).tracksDownloadAllowance().currentValue();
	}

	private File fileToWrite(TrackEndorsement endorsement) {
		String name = new File(endorsement.path).getName();
		return new File(peerTracksFolder(), name);
	}

	private File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

}
