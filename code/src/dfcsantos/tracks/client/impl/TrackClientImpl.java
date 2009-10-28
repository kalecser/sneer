package dfcsantos.tracks.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

class TrackClientImpl implements TrackClient {

	private final Register<Integer> _numberOfTracksFetchedFromPeers = my(Signals.class).newRegister(0); 

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	
	{
		_refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			consumeTrackEndorsement(trackEndorsement);
		}});
	}

	
	private void consumeTrackEndorsement(TrackEndorsement endorsement) {
		if (my(Seals.class).ownSeal().equals(endorsement.publisher())) return;

		final Track endorsedTrack = my(Tracks.class).newTrack(endorsement);
		if (my(RejectedTracksKeeper.class).isRejected(endorsedTrack)) return;

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


	public Signal<Integer> numberOfTracksFetchedFromPeers() {
		return _numberOfTracksFetchedFromPeers.output();
	}

}
