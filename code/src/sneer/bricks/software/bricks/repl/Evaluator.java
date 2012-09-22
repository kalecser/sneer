package sneer.bricks.software.bricks.repl;

public interface Evaluator {

	Object eval(String code);

	void reset();

}
