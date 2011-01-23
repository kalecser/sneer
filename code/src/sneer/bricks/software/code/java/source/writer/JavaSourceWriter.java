package sneer.bricks.software.code.java.source.writer;

import java.io.File;
import java.io.IOException;


public interface JavaSourceWriter {

	File write(String className, String sourceWithoutPackageDeclaration) throws IOException;

}
