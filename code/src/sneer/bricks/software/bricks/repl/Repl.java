package sneer.bricks.software.bricks.repl;

import basis.brickness.Brick;

@Brick
public interface Repl {

	ReplConsole createConsole(ReplLang groovy);

}
