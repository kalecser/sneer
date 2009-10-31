package dfcsantos.tracks.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

class TrackClientImpl implements TrackClient {

	private final Register<Integer> _numberOfTracksFetchedFromPeers = my(Signals.class).newRegister(0);
	private boolean _hasFinishedSharedTracksFolderMapping;
	private boolean _hasFinishedPeerTracksFolderMapping;

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


	public Signal<Integer> numberOfTracksFetchedFromPeers() {
		return _numberOfTracksFetchedFromPeers.output();
	}


	private void consumeTrackEndorsement(TrackEndorsement endorsement) {
		if (!_hasFinishedSharedTracksFolderMapping || !_hasFinishedPeerTracksFolderMapping) return;

		if (my(Seals.class).ownSeal().equals(endorsement.publisher())) return;

		if (my(RejectedTracksKeeper.class).isRejected(endorsement.hash)) return;

		try {
			my(FileClient.class).fetch(fileToWrite(endorsement), endorsement.lastModified, endorsement.hash);
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}

		_numberOfTracksFetchedFromPeers.setter().consume(_numberOfTracksFetchedFromPeers.output().currentValue() + 1);
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
			startMappingOf(peerTracksFolder);
			_hasFinishedPeerTracksFolderMapping = true;
		}});			
	}


	private void startSharedTracksFolderMapping(final File sharedTracksFolder) {
		my(Threads.class).startDaemon("Shared Tracks Folder Mapping", new Runnable() { @Override public void run() {
			startMappingOf(sharedTracksFolder);
			_hasFinishedSharedTracksFolderMapping = true;
		}});			
	}


	private void startMappingOf(final File tracksFolder) {		
		try {
			my(FileMap.class).put(tracksFolder);
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

}
