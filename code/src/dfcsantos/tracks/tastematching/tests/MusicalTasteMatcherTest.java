package dfcsantos.tracks.tastematching.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.software.folderconfig.tests.BrickTest;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

public class MusicalTasteMatcherTest extends BrickTest {

	private final MusicalTasteMatcher _subject = my(MusicalTasteMatcher.class);

	@Test
	public void scoresRegister() {
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
		 * 7) Make sure tracks were received by the remote environment in a particular order based on the taste matching.
		 * 
		 */
	}

}
