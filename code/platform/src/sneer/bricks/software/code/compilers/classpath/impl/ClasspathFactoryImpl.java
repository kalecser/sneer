package sneer.bricks.software.code.compilers.classpath.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.software.code.compilers.classpath.Classpath;
import sneer.bricks.software.code.compilers.classpath.ClasspathFactory;
import sneer.bricks.software.folderconfig.FolderConfig;

class ClasspathFactoryImpl implements ClasspathFactory {

	private Classpath _sneerApi;

	@Override
	public Classpath newClasspath() {
		return new SimpleClasspath();
	}

	@Override
	public Classpath fromClassDir(File rootFolder) {
		return new FolderBasedClasspath(rootFolder);
	}

	@Override
	public Classpath sneerApi() {
		if(_sneerApi == null)
			_sneerApi = findSneerApi();

		return _sneerApi;		
	}

	private Classpath findSneerApi() {
		return new FolderBasedClasspath(my(FolderConfig.class).platformBinFolder().get());
	}

	@Override
	public Classpath fromJarFiles(File... jarFiles) {
		return new JarBasedClasspath(jarFiles);
	}
}
