package sneer.bricks.software.code.compilers.java;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sneer.bricks.software.code.compilers.classpath.Classpath;
import sneer.foundation.brickness.Brick;

@Brick
public interface JavaCompiler {
	
	Result compile(List<File> sourceFiles, File destination) throws IOException;
	
	Result compile(List<File> sourceFiles, File destination, Classpath classpath) throws IOException;
	
}
