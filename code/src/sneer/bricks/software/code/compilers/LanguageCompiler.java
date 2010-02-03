package sneer.bricks.software.code.compilers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;



public interface LanguageCompiler {

	void compile(File srcFolder, File destinationFolder, File... classpath) throws CompilerException, IOException;

	Result compile(Collection<File> sourceFiles, File destination, File...  classpath) throws CompilerException, IOException;


}
