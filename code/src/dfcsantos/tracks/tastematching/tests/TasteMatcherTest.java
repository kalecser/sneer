package dfcsantos.tracks.tastematching.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import dfcsantos.tracks.tastematching.TasteMatcher;

public class TasteMatcherTest extends BrickTestBase {

	private static final Boolean GOOD = true;
	private static final Boolean BAD = false;
	private static final Boolean UNKNOWN = null;

	private TasteMatcher _subject = my(TasteMatcher.class);
	private Contact ana = my(Contacts.class).produceContact("Ana");

	@Test
	public void opinions() {
		assertRating(0f/1, "samba", UNKNOWN);
		assertRating(1f/2, "samba", GOOD);
		assertRating(2f/3, "samba", GOOD);
		assertRating(3f/4, "samba", GOOD);
		assertRating(2f/5, "samba", BAD);

		assertRating(-1f/1, "rock", BAD);
		assertRating(-1f/2, "rock", UNKNOWN);
		assertRating(-2f/3, "rock", BAD);
	}

	
	@Ignore
	@Test
	public void opinionsTrickleToParentFolders() {
		assertRating(1f/1, "rock", GOOD);
		assertRating(-1f/1, "electro", BAD);
		assertRating(1f/1, "samba/raiz", GOOD);
		assertRating(1f/2, "samba/morro", UNKNOWN); //"morro" is unknown, but "samba" parent has a non-zero rating.
		assertRating(1f/3, "samba/carioca", UNKNOWN); //"carioca" is unknown, but "samba" parent has a non-zero rating.
		assertRating(1f/6, "jazz/modern", UNKNOWN); //Is unknown and parent folder also has zero rating so it uses all 6 opinions starting at root (all folders). GOOD + BAD + GOOD + 3 UNKNOWN = 1.
	}

	private void assertRating(float rating, String folder, Boolean opinion) {
		assertRating(rating, ana, folder, opinion);
	}

	private void assertRating(float expectedRating, Contact contact, String folder, Boolean opinion) {
		float rating = _subject.rateEndorsement(contact, folder, opinion);
		assertFloat(expectedRating, rating);
	}

}
