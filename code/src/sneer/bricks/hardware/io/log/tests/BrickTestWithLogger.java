package sneer.bricks.hardware.io.log.tests;


import static sneer.foundation.environments.Environments.my;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scala.actors.threadpool.Arrays;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.brickness.testsupport.BrickTestBase;
import sneer.foundation.environments.Environment;

public abstract class BrickTestWithLogger extends BrickTestBase {
	
	@Bind private final LoggerMocks _loggerMocks = new LoggerMocks(); 
	@SuppressWarnings("unused") @Bind private final Logger _logger = _loggerMocks.newInstance(); 
    

	@Override
	protected void afterFailedtest(Method method, Throwable thrown) {
		try {
			printContext(method, thrown);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected Environment newTestEnvironment(Object... bindings) {
		return super.newTestEnvironment(appendLogger(bindings));
	}


	private Object[] appendLogger(Object... bindings) {
		List<Object> result = new ArrayList<Object>(Arrays.asList(bindings));
		result.add(my(Logger.class));
		return result.toArray();
	}

	
	private void printContext(Method method, Throwable thrown) {
		System.out.println();
		System.out.println();
		System.out.println("Failed Test: -----------------------------------------------------------------------------------------------------");
		System.out.println(getClass().getName() + "  " + method.getName());
		System.out.println();
		System.out.println("Filtered Stack: --------------------------------------------------------------------------------------------------");
		printFilteredStack(thrown);
		System.out.println();
		System.out.println("Log: -------------------------------------------------------------------------------------------------------------");
		my(LoggerMocks.class).printAllKeptMessages();
		System.out.println("==================================================================================================================");
		System.out.println();
		System.out.println();
	}


	private void printFilteredStack(Throwable thrown) {
		for (String line : stackAsLines(thrown))
			if (isInteresting(line))
				System.out.println(line);
	}


	private boolean isInteresting(String stackLine) {
		return !startsWithAny(stackLine,
			"java",
			"sun",
			"$Proxy",
			"org.junit",
			"org.eclipse.jdt.internal",
			"org.prevayler",
			"sneer.bricks.hardware.io.prevalence.nature.impl",
			"sneer.foundation.testsupport",
			"sneer.foundation.brickness.testsupport",
			"sneer.foundation.environments.Environments",
			"sneer.tests.adapters.ProxyInEnvironment"
		);
	}


	private boolean startsWithAny(String stackLine, String... prefixes) {
		for (String prefix : prefixes)
			if (has(stackLine, "at " + prefix)) return true;
		return false;
	}


	private boolean has(String line, String token) {
		return line.indexOf(token) != -1;
	}
	
	private static Collection<String> stackAsLines(Throwable throwable) {
		String stack = stackAsString(throwable);
		return asLines(stack);   //This is done instead of simply calling String.split("\n") because the stack is printed out with different end-of-line delimiters in Windows and Linux.
	}

	private static Collection<String> asLines(String stack) {
		Collection<String> result = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new StringReader(stack));
		while (true) {
			String line = readLine(reader);
			if (line == null) break;
			result.add(line);
		}
		return result;
	}

	private static String stackAsString(Throwable throwable) {
		StringWriter result = new StringWriter();
		throwable.printStackTrace(new PrintWriter(result));
		return result.getBuffer().toString();
	}

	private static String readLine(BufferedReader reader) {
		try {
			return reader.readLine();
		} catch (IOException e) { throw new IllegalStateException(e); }
	}
}
