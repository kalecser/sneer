package dfcsantos.tracks.assessment.tastematcher.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.junit.Test;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.assessment.assessor.TrackAssessor;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

public class MusicTasteMatcherTest extends BrickTest {

	private static int _counter;

	private final Tracks _trackFactory = my(Tracks.class);
	private final TrackAssessor _trackAssessor = my(TrackAssessor.class);

	private final MusicalTasteMatcher _subject = my(MusicalTasteMatcher.class);

	@Test
	public void scoresRegister() throws Refusal {
		Contact igor = my(Contacts.class).addContact("igor");
//		Contact klaus = my(Contacts.class).addContact("klaus");
//		Contact rodrigo = my(Contacts.class).addContact("rodrigo");

		activateMusicalTasteMatcher();

		_trackAssessor.approve(newTrackFrom(igor));
		my(SignalUtils.class).waitForValue(_subject.bestMatch(), igor);

		// Implement: finish test
	}

	private void activateMusicalTasteMatcher() {
		_subject.setOnOffSwitch(my(Signals.class).constant(true));
	}

	private Track newTrackFrom(Contact contact) {
		String nickname = contact.nickname().currentValue();
		return _trackFactory.newTrack(new File(peerTracksFolder(), nickname + "/track" + _counter++ + ".mp3"));
	}

	private File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();  
	}

}
