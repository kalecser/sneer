package sneer.bricks.software.bricks.repl.impl;

import groovy.lang.GroovyShell;
import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplLang;

public class ReplImpl implements Repl {

	private GroovyShell shell = new GroovyShell();

	@Override
	public Object evaluate(ReplLang lang, String text) {
		// binding.setVariable("foo", new Integer(2));
		return shell.evaluate(text);
	}

}
