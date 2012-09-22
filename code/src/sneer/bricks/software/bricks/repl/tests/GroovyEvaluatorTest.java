package sneer.bricks.software.bricks.repl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.Evaluator;
import sneer.bricks.software.bricks.repl.ReplLang;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class GroovyEvaluatorTest extends BrickTestBase {

	private final Repl subject = my(Repl.class);

	@Test
	public void groovyRepl() {
		Evaluator evaluator = createEvaluator();
		String text = "hello";
		Object result = evaluator.eval("'" + text + "'");
		assertEquals(text, result);
	}

	@Test
	public void variablesArePreservedBetweenEvaluations() {
		Evaluator evaluator = createEvaluator();
		String text = "foo = 42";
		Object result = evaluator.eval(text);
		assertEquals(42, result);
		assertEquals(42, evaluator.eval("foo"));
	}
	
	private Evaluator createEvaluator() {
		return subject.newEvaluatorFor(ReplLang.groovy);
	}
}
