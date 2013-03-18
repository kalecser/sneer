package sneer.bricks.software.code.classutils.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.net.URISyntaxException;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.software.code.classutils.ClassUtils;

class ClassUtilsImpl implements ClassUtils {

	@Override
	public File classpathRootFor(Class<?> clazz) {
		final int packageCount = packageName(clazz).split("\\.").length;
		
		File parent = classFile(clazz).getParentFile();
		for (int i=0; i<packageCount; ++i)
			parent = parent.getParentFile();
		
		return parent;
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

	private String packageName(Class<?> clazz) {
		return my(Lang.class).strings().substringBeforeLast(clazz.getName(), ".");
	}
}