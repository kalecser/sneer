package dfcsantos.tracks.tastematching.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import dfcsantos.tracks.endorsements.client.TrackClient;
import dfcsantos.tracks.endorsements.server.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

@Ignore
public class MusicalTasteMatcherTest extends BrickTest {

	@Test (timeout = 4000)
	public void endorsementProcessing() throws IOException {
		/*
		 * 1) Create the following tracks in the 'tmp folder':
		 * 		- Rock 'n' Roll/ACDC/Black Ice/01 - Rock N Roll Train.mp3
		 * 		- Rock 'n' Roll/ACDC/Black Ice/02 - Skies On Fire.mp3
		 * 		- Rock 'n' Roll/ACDC/Black Ice/03 - Big Jack.mp3
		 * 		- etc
		 * 2) Set the 'shared tracks folder' to point to 'tmp folder'
		 * 3) Activate the TrackEndorser
		 * 4) Create remote environment to receive endorsements
		 * 5) Insert some of the tracks created by the local environment in the file map of the remote environment 
		 * 6) Activate remote environment's TrackClient
		 * 7) Make sure tracks are received by the remote environment in a particular order based on the taste matching.
		 * 
		 */

		final List<File> sampleTracks = createSampleTracks(
			"Rock 'n' Roll/ACDC/Black Ice/01 - Rock N Roll Train.mp3",
			"Rock 'n' Roll/ACDC/Black Ice/02 - Skies On Fire.mp3",
			"Rock 'n' Roll/ACDC/Black Ice/03 - Big Jack.mp3"
		);

		my(TracksFolderKeeper.class).setSharedTracksFolder(tmpFolder());

		activateTrackEndorser();

		Environment remoteEnvironment = newTestEnvironment(my(TupleSpace.class));
		configureFoldersOf(remoteEnvironment);

		Environments.runWith(remoteEnvironment, new ClosureX<IOException>() { @Override public void run() throws IOException {
			File sharedTasteTrack = sampleTracks.get(0);
			keep(sharedTasteTrack);
			activateTrackClient();
		}});

	}

	private List<File> createSampleTracks(String... tracks) throws IOException {
		List<File> sampleTracks = new ArrayList<File>();
		for (String track : tracks) {
			File sampleTrack = new File(tmpFolder(), track);
			my(IO.class).files().writeString(sampleTrack, track);
			sampleTracks.add(sampleTrack);
		}
		return sampleTracks;
	}

	private void activateTrackEndorser() {
		my(TrackEndorser.class).setOnOffSwitch(my(Signals.class).constant(true));
	}

	private void activateTrackClient() {
		my(TrackClient.class).setOnOffSwitch(my(Signals.class).constant(true));
	}

	private void configureFoldersOf(Environment remoteEnvironment) {
		configureStorageFolder(remoteEnvironment, "remote/data");
		configureTmpFolder(remoteEnvironment, "remote/tmp");
	}

	private void keep(File sharedTasteTrack) throws IOException {
		my(FileMap.class).putFile(sharedTasteTrack, hashFor(sharedTasteTrack));
	}

	private Sneer1024 hashFor(File track) throws IOException {
		return my(Crypto.class).digest(track);
	}

}
