package sneer.bricks.software.code.java.source.writer;

import java.io.IOException;


public interface JavaSourceWriter {

	void write(String className, String sourceWithoutPackageDeclaration) throws IOException;

}
