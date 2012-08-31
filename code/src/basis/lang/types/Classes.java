package basis.lang.types;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Classes {

	private static final int DOT_CLASS = ".class".length();
	private static final Class<?>[] CLASS_ARRAY_TYPE = new Class[]{};


	public static Class<?>[] allInterfacesOf(Class<?> clazz) {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		while (clazz != Object.class) {
			for (Class<?> intrface :clazz.getInterfaces())
				result.add(intrface);
			clazz = clazz.getSuperclass();
		}
		return result.toArray(CLASS_ARRAY_TYPE);
	}

	
	public static File classpathRootFor(Class<?> clazz) {
		File result = fileFor(clazz);
		int depth = clazz.getName().split("\\.").length;
		while (depth-- != 0)
			result = result.getParentFile();
		return result;
	}

	
	public static File fileFor(Class<?> clazz) {
		URL url = clazz.getResource(clazz.getSimpleName() + ".class");
		if (url == null)
			throw new IllegalStateException("Class file for " + clazz + " not found");
		return new File(url.getFile());
	}
	
	
	public static String className(String classpathRoot, String classFilePath) {
		if (!classFilePath.startsWith(classpathRoot)) throw new IllegalStateException("Class file: " + classFilePath + " should be inside subfolder of: " + classpathRoot);
		int afterRoot = classpathRoot.length() + 1;
		int beforeDotClass = classFilePath.length() - DOT_CLASS;
		return classFilePath.substring(afterRoot, beforeDotClass).replace('/', '.').replace('\\', '.');
	}

}
