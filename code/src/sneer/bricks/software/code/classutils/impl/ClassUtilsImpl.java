package sneer.bricks.software.code.classutils.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.net.URISyntaxException;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.software.code.classutils.ClassUtils;

class ClassUtilsImpl implements ClassUtils {

	@Override
	public File classpathRootFor(Class<?> clazz) {
		return rootFolderFor(clazz, classFile(clazz));
	}

	@Override
	public File classFile(Class<?> clazz) {
		try {
			return new File(clazz.getResource(clazz.getSimpleName() + ".class").toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String relativeClassFileName(Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".class";
	}

	@Override
	public String relativeJavaFileName(Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".java";
	}

	private File rootFolderFor(Class<?> clazz, File classFile) {
		final int packageCount = packageName(clazz).split("\\.").length;

		File parent = classFile.getParentFile();
		for (int i=0; i<packageCount; ++i)
			parent = parent.getParentFile();

		return parent;
	}

	private String packageName(Class<?> clazz) {
		return my(Lang.class).strings().substringBeforeLast(clazz.getName(), ".");
	}
}