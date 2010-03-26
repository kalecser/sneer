package dfcsantos.tracks.tastematching.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

public class MusicalTasteMatcherTest extends BrickTest {

	private MusicalTasteMatcher _subject = my(MusicalTasteMatcher.class);

	@Test (timeout = 4000)
	public void endorsementProcessing() throws Refusal {
		Contact neide = my(Contacts.class).addContact("Neide");

		String folderOfRocketManAlbum = "My Fauvorites Songs/Pop/Elton John/Rocket Man";
		_subject.processEndorsement(neide, folderOfRocketManAlbum, false);
		assertEquals(0, _subject.ratingFor(neide, folderOfRocketManAlbum), 0.001);

		String folderOfVivaLaVidaAlbum = "My Fauvorites Songs/Pop/Coldplay/Viva La Vida";
		_subject.processEndorsement(neide, folderOfVivaLaVidaAlbum, false);
		assertEquals(0, _subject.ratingFor(neide, folderOfVivaLaVidaAlbum), 0.001);
		_subject.processEndorsement(neide, folderOfVivaLaVidaAlbum, true);
		assertEquals(1/2, _subject.ratingFor(neide, folderOfVivaLaVidaAlbum), 0.001);
		_subject.processEndorsement(neide, folderOfVivaLaVidaAlbum, true);
		assertEquals(2/3, _subject.ratingFor(neide, folderOfVivaLaVidaAlbum), 0.001);
		_subject.processEndorsement(neide, folderOfVivaLaVidaAlbum, true);
		assertEquals(3/4, _subject.ratingFor(neide, folderOfVivaLaVidaAlbum), 0.001);
		_subject.processEndorsement(neide, folderOfVivaLaVidaAlbum, true);
		assertEquals(4/5, _subject.ratingFor(neide, folderOfVivaLaVidaAlbum), 0.001);

		Contact mister = my(Contacts.class).addContact("Mr. Mister");

		String folderOfFrankSinatra = "My Music/Jazz/Frank Sinatra/The Best Of The Columbia Years";
		_subject.processEndorsement(mister, folderOfFrankSinatra, true);
		assertEquals(1, _subject.ratingFor(mister, folderOfFrankSinatra), 0.001);
		_subject.processEndorsement(mister, folderOfFrankSinatra, false);
		assertEquals(1/2, _subject.ratingFor(mister, folderOfFrankSinatra), 0.001);
		_subject.processEndorsement(mister, folderOfFrankSinatra, false);
		assertEquals(1/3, _subject.ratingFor(mister, folderOfFrankSinatra), 0.001);
	}

}
