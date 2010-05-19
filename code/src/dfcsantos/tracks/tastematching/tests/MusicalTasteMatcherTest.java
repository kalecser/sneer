package dfcsantos.tracks.tastematching.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

public class MusicalTasteMatcherTest extends BrickTestWithFiles {

	private MusicalTasteMatcher _subject = my(MusicalTasteMatcher.class);

	@Test (timeout = 4000)
	public void endorsementProcessing() throws Refusal {
		Contact neide = my(Contacts.class).addContact("Neide");

		assertRating(0, neide, "My Favourites Songs/Pop/Elton John/Rocket Man", false);

		String folderOfVivaLaVidaAlbum = "My Favourites Songs/Pop/Coldplay/Viva La Vida";
		assertRating(0  , neide, folderOfVivaLaVidaAlbum, false);
		assertRating(1/2, neide, folderOfVivaLaVidaAlbum, true);
		assertRating(2/3, neide, folderOfVivaLaVidaAlbum, true);
		assertRating(3/4, neide, folderOfVivaLaVidaAlbum, true);
		assertRating(4/5, neide, folderOfVivaLaVidaAlbum, true);

		Contact mister = my(Contacts.class).addContact("Mr. Mister");

		String folderOfFrankSinatra = "My Music/Jazz/Frank Sinatra/The Best Of The Columbia Years";
		assertRating(1  , mister, folderOfFrankSinatra, true);
		assertRating(1/2, mister, folderOfFrankSinatra, false);
		assertRating(1/3, mister, folderOfFrankSinatra, false);
	}

	private void assertRating(float expectedRating, Contact contact, String folder, boolean isKnownTrack) {
		String nickname = contact.nickname().currentValue();
		_subject.processEndorsement(nickname, folder, isKnownTrack);
		assertFloat(expectedRating, _subject.ratingFor(nickname, folder));
	}

}
