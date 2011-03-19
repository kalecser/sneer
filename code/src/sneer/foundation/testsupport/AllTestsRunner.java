package sneer.foundation.testsupport;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import sneer.foundation.lang.types.Classes;

public final class AllTestsRunner {
	
	private static File _classpathRoot;

	public static void main(String[] args) {
		Class<?>[] classes = findTestClasses();
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener());
		junit.run(classes);
	}

	private static Class<?>[] findTestClasses() {
		_classpathRoot = Classes.classpathRootFor(AllTestsRunner.class);
		List<Class<?>> result = convertToClasses(testClassFileNames());
		return result.toArray(new Class[result.size()]);
	}

	private static List<String> testClassFileNames() {
		List<String> result = new ArrayList<String>();
		accumulateTestClassFileNames(result, _classpathRoot);
		return result;
	}

	private static void accumulateTestClassFileNames(List<String> classFiles, File folder) {
		for (File candidate : folder.listFiles())
			if (candidate.isDirectory())
				accumulateTestClassFileNames(classFiles, candidate);
			else
				accumulateTestClassFileName(classFiles, candidate);
	}

	private static void accumulateTestClassFileName(List<String> classFiles, File file) {
		String name = file.getName();
		if (name.endsWith("Test.class"))
			classFiles.add(file.getAbsolutePath());
	}

	private static List<Class<?>> convertToClasses(final List<String> classFilePaths) {
		List<Class<?>> result = new ArrayList<Class<?>>();

		String rootPath = _classpathRoot.getAbsolutePath();

		for (String filePath : classFilePaths) {
			if (!filePath.startsWith(rootPath)) throw new IllegalStateException("Path: " + filePath + " is not in root path: " + rootPath);
			String className = filePath.substring(rootPath.length() + 1, filePath.length() - 6).replace('/', '.').replace('\\', '.');
			Class<?> c;
			try {
				c = Class.forName(className);
				System.out.println(c);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
			if (!Modifier.isAbstract(c.getModifiers())) {
				result.add(c);
			}
		}
		return result;
	}

}
