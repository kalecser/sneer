package sneer.bricks.software.bricks.repl.tests;


import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.software.bricks.repl.Evaluator;
import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
@Ignore
public class ReplConsoleTest extends BrickTestBase {

	@Test
	public void exceptionsAreReported() {
		final String code = "code";
		final IllegalStateException expectedException = new IllegalStateException("oops...");
		
		final Evaluator evaluator = mock(Evaluator.class);
		ReplConsole console = my(Repl.class).newConsoleFor(evaluator);
		checking(new Expectations() {{
			exactly(1).of(evaluator).eval(code);
				will(throwException(expectedException));
		}});
		
		String resultingText = console.eval(code);
		assertTrue(resultingText.startsWith(expectedException.toString()));
	}
}
