package sneer.bricks.software.code.java.source.writer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;

class JavaSourceWriterImpl implements JavaSourceWriter {

	private final File _srcFolder;

	JavaSourceWriterImpl(File srcFolder) {
		_srcFolder = srcFolder;
	}

	@Override
	public File write(String className, String sourceWithoutPackage) throws IOException {
		String source = "package " + packageName(className) + ";\n\n" + sourceWithoutPackage;
		File javaFile = javaFile(className);
		my(IO.class).files().writeString(javaFile, source);
		return javaFile;
	}

	private String packageName(String className) {
		return className.substring(0, className.lastIndexOf('.'));
	}

	public File javaFile(String className) {
		return new File(_srcFolder, className.replace('.', '/') + ".java");
	}

}
