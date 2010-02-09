package sneer.bricks.software.code.compilers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;



public interface LanguageCompiler {

	Result compile(Collection<File> sourceFiles, File destination, File...  classpath) throws CompilerException, IOException;


}
