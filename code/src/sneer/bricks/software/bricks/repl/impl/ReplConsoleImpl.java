package sneer.bricks.software.bricks.repl.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import sneer.bricks.software.bricks.repl.Evaluator;
import sneer.bricks.software.bricks.repl.ReplConsole;

public class ReplConsoleImpl implements ReplConsole {

	private Evaluator evaluator;

	public ReplConsoleImpl(Evaluator evaluator) {
		super();
		this.evaluator = evaluator;
	}

	@Override
	public String eval(String code) {
		String stringResult = ReplConsole.RESULT_PREFIX;
		try {
			Object result = evaluator.eval(code);
			stringResult += result == null ? "null" : result.toString();
		} catch (Throwable e) {
			stringResult += exceptionToString(e);
		}
		return stringResult;
	}

	private String exceptionToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
