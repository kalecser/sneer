package sneer.bricks.software.bricks.repl.impl;

import groovy.lang.GroovyShell;
import sneer.bricks.software.bricks.repl.Evaluator;

public class GroovyReplConsole implements Evaluator {

	private final GroovyShell shell = new GroovyShell();

	@Override
	public Object eval(String code) {
		return shell.evaluate(code);
	}

}
