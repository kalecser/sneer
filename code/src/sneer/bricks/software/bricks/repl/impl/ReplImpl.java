package sneer.bricks.software.bricks.repl.impl;

import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.bricks.repl.ReplLang;

public class ReplImpl implements Repl {

	@Override
	public ReplConsole createConsole(ReplLang language) {
		return new GroovyReplConsole();
	}

}
