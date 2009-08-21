package sneer.bricks.software.code.compilers.java;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import sneer.foundation.brickness.Brick;

@Brick
public interface JavaCompiler {
	
	void compile(File srcFolder, File destinationFolder, File... classpath) throws JavaCompilerException, IOException;

	Result compile(Collection<File> sourceFiles, File destination, File...  classpath) throws JavaCompilerException, IOException;

}
