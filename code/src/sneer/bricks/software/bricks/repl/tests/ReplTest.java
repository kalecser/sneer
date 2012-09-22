package sneer.bricks.software.bricks.repl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplLang;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class ReplTest extends BrickTestBase {

	private final Repl subject = my(Repl.class);

	@Test
	public void testHello() {
		String text = "hello";
		Object result = subject.evaluate(ReplLang.groovy, "'" + text + "'");
		assertEquals(text, result);
	}

}
