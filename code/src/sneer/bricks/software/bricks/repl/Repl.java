package sneer.bricks.software.bricks.repl;

import basis.brickness.Brick;

@Brick
public interface Repl {

	Object evaluate(ReplLang lang, String text);

}
