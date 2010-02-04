package sneer.bricks.software.code.compilers;

import java.util.List;


public interface Result {

	boolean success();

	List<CompilationError> errors();

	String errorString();
}
