package sneer.bricks.software.bricks.repl.impl;

import sneer.bricks.software.bricks.repl.Repl;
import sneer.bricks.software.bricks.repl.Evaluator;
import sneer.bricks.software.bricks.repl.ReplConsole;
import sneer.bricks.software.bricks.repl.ReplLang;

public class ReplImpl implements Repl {

	@Override
	public Evaluator newEvaluatorFor(ReplLang language) {
		return new GroovyReplConsole();
	}

	@Override
	public ReplConsole newConsoleFor(Evaluator evaluator) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

}
