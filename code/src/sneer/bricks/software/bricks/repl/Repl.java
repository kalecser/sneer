package sneer.bricks.software.bricks.repl;

import basis.brickness.Brick;

// TODO: add imports
// TODO: reset engine
@Brick
public interface Repl {

	Evaluator newEvaluatorFor(ReplLang lang);

	ReplConsole newConsoleFor(Evaluator evaluator);

}
