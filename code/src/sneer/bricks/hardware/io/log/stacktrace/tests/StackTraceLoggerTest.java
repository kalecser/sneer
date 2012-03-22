package sneer.bricks.hardware.io.log.stacktrace.tests;

import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import basis.brickness.testsupport.Bind;
import basis.brickness.testsupport.BrickTestWithMocks;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;



public class StackTraceLoggerTest extends BrickTestWithMocks {

	@Bind private final Logger _logger = mock(Logger.class); 
	private final StackTraceLogger _subject = my(StackTraceLogger.class);
	
	@Test
	public void stackTraceLogging() {
		checking(new Expectations() {{
			exactly(1).of(_logger).log(with(any(String.class)), with(any(Object.class)));
			will(new CustomAction("log") { @Override public Object invoke(Invocation invocation) throws Throwable {
				String message = (String) invocation.getParameter(0);
				assertTrue(message.startsWith("fooMessage"));
				assertTrue(message.contains("Thread state: RUNNABLE"));
				assertTrue(message.contains("stackTraceLogging")); //Name of this test method must be on the stack.
				return null;
			}});
		}});
		_subject.logStackTrace(Thread.currentThread(), "fooMessage");
	}
	
	
}
