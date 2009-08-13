package sneer.bricks.software.code.compilers.java;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sneer.foundation.brickness.Brick;

@Brick
public interface JavaCompiler {
	
	void compile(File srcFolder, File destinationFolder, File... classpath) throws JavaCompilerException, IOException;

	//These deprecated methods are used only by tests.
	@Deprecated	Result compile(List<File> sourceFiles, File destination) throws IOException;
	@Deprecated	Result compile(List<File> sourceFiles, File destination, File...  classpath) throws IOException;

}
