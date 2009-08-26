package sneer.bricks.software.code.java.source.writer.impl;

import java.io.File;

import sneer.bricks.software.code.java.source.writer.JavaSourceWriter;
import sneer.bricks.software.code.java.source.writer.JavaSourceWriters;

class JavaSourceWritersImpl implements JavaSourceWriters {

	@Override
	public JavaSourceWriter newInstance(File srcFolder) {
		return new JavaSourceWriterImpl(srcFolder);
	}


}
