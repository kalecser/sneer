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
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.endorsements.TrackEndorsement;

class TrackClientImpl implements TrackClient {

	private final Register<Integer> _numberOfTracksFetchedFromPeers = my(Signals.class).newRegister(0); 

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	
	{
		_refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			consumeTrackEndorsement(trackEndorsement);
		}});
	}

	
	private void consumeTrackEndorsement(TrackEndorsement track) {
		if (my(Seals.class).ownSeal().equals(track.publisher())) return;
		
		try {
			my(FileClient.class).fetch(fileToWrite(track), track.lastModified, track.hash);
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}

		_numberOfTracksFetchedFromPeers.setter().consume(_numberOfTracksFetchedFromPeers.output().currentValue() + 1);
	}

	
	private File fileToWrite(TrackEndorsement track) throws IOException {
		String name = new File(track.path).getName();
		File candidatesFolder = candidatesFolder();
		return new File(candidatesFolder, name);
	}


	private File candidatesFolder() throws IOException {
		File result = new File(my(FolderConfig.class).tmpFolderFor(TrackClient.class), "candidates");
		if (!result.exists() && !result.mkdir()) throw new IOException("Unable to create track candidates directory:"+result);
		return result;
	}


	public Signal<Integer> numberOfTracksFetchedFromPeers() {
		return _numberOfTracksFetchedFromPeers.output();
	}

}
