package sneer.bricks.software.bricks.repl.tests;

import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.software.bricks.repl.Evaluator;
import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class ReplConsoleTest extends BrickTestBase {

	@Test
	public void exceptionsAreReported() {
		final String code = "code";
		final IllegalStateException expectedException = new IllegalStateException(
				"oops...");

		final Evaluator evaluator = mock(Evaluator.class);
		ReplConsole console = my(Repl.class).newConsoleFor(evaluator);
		checking(new Expectations() {
			{
				exactly(1).of(evaluator).eval(code);
				will(throwException(expectedException));
			}
		});

		String resultingText = console.eval(code);
		assertTrue(resultingText.startsWith(ReplConsole.RESULT_PREFIX + expectedException.toString()));
	}
	
	@Test
	public void useConsoleHappyDay() {
		final String code = "'hello'";
		
		final Evaluator evaluator = mock(Evaluator.class);
		ReplConsole console = my(Repl.class).newConsoleFor(evaluator);
		checking(new Expectations() {
			{
				exactly(1).of(evaluator).eval(code);
				will(returnValue("hello"));
			}
		});
		
		String resultingText = console.eval(code);
		assertEquals(
				ReplConsole.RESULT_PREFIX + code.replaceAll("'", ""), 
				resultingText);
	}
	
	@Test
	public void useConsoleWithNullReturn() {
		
		final Evaluator evaluator = mock(Evaluator.class);
		ReplConsole console = my(Repl.class).newConsoleFor(evaluator);
		checking(new Expectations() {
			{
				exactly(1).of(evaluator).eval(null);
				will(returnValue(null));
			}
		});
		
		String resultingText = console.eval(null);
		assertEquals(
				ReplConsole.RESULT_PREFIX + "null", 
				resultingText);
	}
}
