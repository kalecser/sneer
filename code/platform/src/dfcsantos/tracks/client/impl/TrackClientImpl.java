package dfcsantos.tracks.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

public class TrackClientImpl implements TrackClient {
	
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	
	{
		_refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			consumeTrackEndorsement(trackEndorsement);
		}});
	}

	
	private void consumeTrackEndorsement(TrackEndorsement track) {
		if (my(Seals.class).ownSeal().equals(track.publisher())) return;
		
		my(FileClient.class).fetchToCache(track.hash);
		try {

			my(FileWriter.class).writeAtomicallyTo(fileToWrite(track), track.lastModified, track.hash);

		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	
	private File fileToWrite(TrackEndorsement track) {
		String name = new File(track.path).getName();
		return new File(my(OwnTracksFolderKeeper.class).peerTracksFolder().currentValue(), name);
	}
}
