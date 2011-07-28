package dfcsantos.tracks.tastematching.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

public class MusicalTasteMatcherTest extends BrickTestBase {

	private static final Boolean GOOD = true;
	private static final Boolean UNKNOWN = null;

	private MusicalTasteMatcher _subject = my(MusicalTasteMatcher.class);

	@Test (timeout = 4000)
	public void endorsementProcessing() throws Refusal {
		Contact neide = my(Contacts.class).addContact("Neide");

		assertRating(0, neide, "My Favourites Songs/Pop/Elton John/Rocket Man", UNKNOWN);

		String folderOfVivaLaVidaAlbum = "My Favourites Songs/Pop/Coldplay/Viva La Vida";
		assertRating(0  , neide, folderOfVivaLaVidaAlbum, UNKNOWN);
		assertRating(1/2, neide, folderOfVivaLaVidaAlbum, GOOD);
		assertRating(2/3, neide, folderOfVivaLaVidaAlbum, GOOD);
		assertRating(3/4, neide, folderOfVivaLaVidaAlbum, GOOD);
		assertRating(4/5, neide, folderOfVivaLaVidaAlbum, GOOD);

		Contact mister = my(Contacts.class).addContact("Mr. Mister");

		String folderOfFrankSinatra = "My Music/Jazz/Frank Sinatra/The Best Of The Columbia Years";
		assertRating(1  , mister, folderOfFrankSinatra, GOOD);
		assertRating(1/2, mister, folderOfFrankSinatra, UNKNOWN);
		assertRating(1/3, mister, folderOfFrankSinatra, UNKNOWN);
	}

	private void assertRating(float expectedRating, Contact contact, String folder, Boolean opinion) {
		float rating = _subject.rateEndorsement(contact, folder, opinion);
		assertFloat(expectedRating, rating);
	}

}
