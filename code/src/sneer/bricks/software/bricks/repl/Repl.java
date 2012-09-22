package sneer.bricks.software.bricks.repl;

import basis.brickness.Brick;

// TODO: catch exceptions
// TODO: add imports
// TODO: reset engine
@Brick
public interface Repl {

	Evaluator newEvaluatorFor(ReplLang groovy);

	ReplConsole newConsoleFor(Evaluator evaluator);

}
