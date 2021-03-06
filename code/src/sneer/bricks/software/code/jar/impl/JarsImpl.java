package sneer.bricks.software.code.jar.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.code.jar.JarBuilder;
import sneer.bricks.software.code.jar.Jars;

import static basis.environments.Environments.my;

public class JarsImpl implements Jars {

	@Override
	public JarBuilder builder(File file) throws IOException {
		return new JarBuilderImpl(file);
	}

	@Override
	public void build(File jarFile, File binFolder) throws IOException {
		JarBuilder builder = builder(jarFile);
		int prefixLength = binFolder.getAbsolutePath().length() + 1;
		for (File file : listFiles(binFolder))
			builder.add(file.getAbsolutePath().substring(prefixLength), file);
		builder.close();
	}

	private Collection<File> listFiles(File binFolder) {
		return my(IO.class).files().listFiles(binFolder, null, true);
	}
}

class JarBuilderImpl implements JarBuilder{

	private final JarOutputStream _out;
	private final FileOutputStream _fileOutputStream;
	
	public JarBuilderImpl(File file) throws IOException {
		file.getParentFile().mkdirs();
		_fileOutputStream = new FileOutputStream(file);
		_out = new JarOutputStream(new BufferedOutputStream(_fileOutputStream));
	}
	
	@Override
	public void add(String entryName, File file) throws IOException {
		add(entryName, new FileInputStream(file.getAbsolutePath()));
	}

	private void add(String entryName, InputStream is) throws IOException {
		entryName = entryName.replace('\\', '/');
		JarEntry je = new JarEntry(entryName);
		_out.putNextEntry(je);
		byte[] buffer = new byte[1024*4];
		int n = 0;
		while (-1 != (n = is.read(buffer)))  _out.write(buffer, 0, n); 
		is.close();
	}
	
	@Override
	public void close() {
		try {
			_out.close();
			_fileOutputStream.close();
		} catch (Throwable ignore) { }
	}
}