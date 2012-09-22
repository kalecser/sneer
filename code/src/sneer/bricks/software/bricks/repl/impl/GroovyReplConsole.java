package sneer.bricks.software.bricks.repl.impl;

import groovy.lang.GroovyShell;
import sneer.bricks.software.bricks.repl.ReplConsole;

public class GroovyReplConsole implements ReplConsole {

	private final GroovyShell shell = new GroovyShell();

	@Override
	public Object evaluate(String code) {
		return shell.evaluate(code);
	}

}
