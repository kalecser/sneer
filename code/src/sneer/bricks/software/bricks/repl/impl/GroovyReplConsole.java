package sneer.bricks.software.bricks.repl.impl;

import groovy.lang.GroovyShell;
import sneer.bricks.software.bricks.repl.ReplConsole;

public class GroovyReplConsole implements ReplConsole {

	@Override
	public Object evaluate(String code) {
		return new GroovyShell().evaluate(code);
	}

}
