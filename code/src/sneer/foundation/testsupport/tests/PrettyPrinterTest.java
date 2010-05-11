package sneer.foundation.testsupport.tests;

import org.junit.Test;

import sneer.foundation.lang.Functor;
import sneer.foundation.testsupport.AssertUtils;
import sneer.foundation.testsupport.PrettyPrinter;

public class PrettyPrinterTest extends AssertUtils {

	@Test
	public void capitalizeStrings() {
		// Register "Capitalizer"
		PrettyPrinter.registerFor(String.class, new Functor<String, String>() { @Override public String evaluate(String string) {
			return string.toUpperCase();
		}});
		assertEquals("TEST PHRASE", PrettyPrinter.toString("test phrase"));

		// Back to original behavior
		PrettyPrinter.registerFor(String.class, new Functor<String, String>() { @Override public String evaluate(String string) {
			return string;
		}});
		assertEquals("test phrase", PrettyPrinter.toString("test phrase"));
	}

}
