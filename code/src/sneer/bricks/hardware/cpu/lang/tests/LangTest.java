package sneer.bricks.hardware.cpu.lang.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class LangTest extends BrickTestBase {

	private final Lang _subject = my(Lang.class);

	@Test
	public void insertionOfSeparatorsIntoString() {
		final String original = "123456789ABCDEFGHIJKLMNO";

		assertEquals(
			"1 2 3 4 5 6 7 8 9 A B C D E F G H I J K L M N O",
			_subject.strings().insertSpacedSeparators(original, " ", 1)
		);

		assertEquals(
			"1, 2, 3, 4, 5, 6, 7, 8, 9, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O",
			_subject.strings().insertSpacedSeparators(original, ", ", 1)
		);

		assertEquals(
			"12 / 34 / 56 / 78 / 9A / BC / DE / FG / HI / JK / LM / NO",
			_subject.strings().insertSpacedSeparators(original, " / ", 2)
		);

		assertEquals(
			"123 -- 456 -- 789 -- ABC -- DEF -- GHI -- JKL -- MNO",
			_subject.strings().insertSpacedSeparators(original, " -- ", 3)
		);

		assertEquals(
			"1234#5678#9ABC#DEFG#HIJK#LMNO",
			_subject.strings().insertSpacedSeparators(original, "#", 4)
		);

		assertEquals(
			"12345 $6789A $BCDEF $GHIJK $LMNO",
			_subject.strings().insertSpacedSeparators(original, " $", 5)
		);

	}

}
