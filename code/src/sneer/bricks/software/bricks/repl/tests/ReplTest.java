package sneer.bricks.software.bricks.repl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.bricks.repl.ReplLang;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class ReplTest extends BrickTestBase {

	private final Repl subject = my(Repl.class);

	@Test
	public void groovyRepl() {
		ReplConsole console = subject.createConsole(ReplLang.groovy);
		String text = "hello";
		Object result = console.evaluate("'" + text + "'");
		assertEquals(text, result);
	}

}
