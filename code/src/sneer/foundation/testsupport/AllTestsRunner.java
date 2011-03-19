package sneer.foundation.testsupport;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import sneer.foundation.lang.types.Classes;

public final class AllTestsRunner {
	
//	Usage in agitos_server:
//	cd /agitos_server/git_repos/sneer/installer/build/bin/sneer/foundation/testsupport/lib
//	java -cp /agitos_server/git_repos/sneer/installer/build/bin:junit-4.4.jar:jmock-2.5.1.jar:jmock-junit4-2.5.1.jar:hamcrest-core-1.1.jar:hamcrest-library-1.1.jar sneer.foundation.testsupport.AllTestsRunner
	
	private static File _classpathRoot;

	public static void main(String[] args) {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener());
		junit.run(findTestClasses());
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
			String className = Classes.className(rootPath, filePath);
			Class<?> c = classForName(className);
			if (!Modifier.isAbstract(c.getModifiers()))
				result.add(c);
		}
		return result;
	}

	private static Class<?> classForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

}
