package sneer.bricks.software.bricks.repl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.bricks.repl.ReplLang;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class GroovyConsoleTest extends BrickTestBase {

	private final Repl subject = my(Repl.class);

	@Test
	public void groovyRepl() {
		ReplConsole console = createConsole();
		String text = "hello";
		Object result = console.evaluate("'" + text + "'");
		assertEquals(text, result);
	}

	@Test
	public void variablesArePreservedBetweenEvaluations() {
		ReplConsole console = createConsole();
		String text = "foo = 42";
		Object result = console.evaluate(text);
		assertEquals(42, result);
		assertEquals(42, console.evaluate("foo"));
	}
	
	private ReplConsole createConsole() {
		return subject.createConsole(ReplLang.groovy);
	}
}
