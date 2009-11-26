package dfcsantos.tracks.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.client.Download;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.client.TrackEndorsement;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

class TrackClientImpl implements TrackClient {

	private final Register<Integer> _numberOfDownloadedTracks = my(Signals.class).newRegister(0);
	private final Latch _hasFinishedSharedTracksFolderMapping = my(Latches.class).produce();
	private final Latch _hasFinishedPeerTracksFolderMapping = my(Latches.class).produce();

	private List<Download> _downloads = Collections.synchronizedList(new ArrayList<Download>());

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC2;


	{
		_refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			consumeTrackEndorsement(trackEndorsement);
		}});

		startPeerTracksFolderMapping(my(TracksFolderKeeper.class).peerTracksFolder());

		_refToAvoidGC2 = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File sharedTracksFolder) {
			startSharedTracksFolderMapping(sharedTracksFolder);
		}});
	}


	public Signal<Integer> numberOfDownloadedTracks() {
		return _numberOfDownloadedTracks.output();
	}


	private void consumeTrackEndorsement(TrackEndorsement endorsement) {
		_hasFinishedSharedTracksFolderMapping.waitTillOpen();
		_hasFinishedPeerTracksFolderMapping.waitTillOpen();

		if (my(Seals.class).ownSeal().equals(endorsement.publisher())) return;
		if (my(RejectedTracksKeeper.class).isRejected(endorsement.hash)) return;

		if (_downloads.size() >= 3) return;
		final Download download = my(FileClient.class).startFileDownload(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash);
		_downloads.add(download);

		my(Threads.class).startDaemon("Waiting for Download", new Runnable() { @Override public void run() {
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


	private File fileToWrite(TrackEndorsement endorsement) {
		String name = new File(endorsement.path).getName();
		return new File(peerTracksFolder(), name);
	}


	private File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}


	private void startPeerTracksFolderMapping(final File peerTracksFolder) {
		my(Threads.class).startDaemon("Peer Tracks Folder Mapping", new Runnable() { @Override public void run() {
			map(peerTracksFolder);
			_hasFinishedPeerTracksFolderMapping.open();
		}});			
	}


	private void startSharedTracksFolderMapping(final File sharedTracksFolder) {
		my(Threads.class).startDaemon("Shared Tracks Folder Mapping", new Runnable() { @Override public void run() {
			map(sharedTracksFolder);
			_hasFinishedSharedTracksFolderMapping.open();
		}});			
	}


	private void map(final File tracksFolder) {		
		try {
			my(FileMap.class).put(tracksFolder);
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

}
