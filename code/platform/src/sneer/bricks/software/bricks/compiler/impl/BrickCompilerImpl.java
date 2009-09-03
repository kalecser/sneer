package sneer.bricks.software.bricks.compiler.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.FileFilters;
import sneer.bricks.hardware.io.IO.Files;
import sneer.bricks.hardware.io.IO.Filter;
import sneer.bricks.software.bricks.compiler.BrickCompiler;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.java.JavaCompilerException;
import sneer.bricks.software.folderconfig.FolderConfig;

class BrickCompilerImpl implements BrickCompiler {

	@Override
	public void compile(File srcFolder, File destFolder) throws IOException {
		
		Collection<File> apiFiles = brickApiFilesIn(srcFolder);
		compileApi(apiFiles, destFolder);
		compileBricks(brickFolders(apiFiles), destFolder);
		
		copyResources(srcFolder, destFolder);
		
	}

	private void copyResources(File srcFolder, File destFolder) throws IOException {
		Filter nonJavaFiles = fileFilters().not(fileFilters().suffix(".java"));
		files().copyFolder(srcFolder, destFolder, nonJavaFiles);
	}

	private Collection<File> brickFolders(Collection<File> apiFiles) {
		HashSet<File> result = new HashSet<File>();
		for (File file : apiFiles)
			result.add(file.getParentFile());
		return result;
	}
	
	private void compileBricks(Collection<File> brickFolders, File destinationFolder) throws IOException {
		File tmpFolder = cleanTmpBrickImplFolder();
		
		File[] testClasspath = testClassPath(destinationFolder);
		
		for (File brickFolder : brickFolders) {
			File[] implClasspath = brickClasspathWith(destinationFolder, new File(brickFolder, "impl/lib"));
			compileBrick(brickFolder, tmpFolder, testClasspath, implClasspath);
		}
		
		copyFolder(tmpFolder, destinationFolder);
	}

	private File[] toFileArray(List<File> testClasspath) {
		return testClasspath.toArray(new File[testClasspath.size()]);
	}

	private File[] testClassPath(File destinationFolder) {
		return brickClasspathWith(destinationFolder, foundationSrcFolder());
	}

	private File[] brickClasspathWith(File destinationFolder, File libFolder) {
		if (!libFolder.exists()) {
			return new File[] { destinationFolder };
		}
		
		List<File> classpath = new ArrayList<File>();
		classpath.add(destinationFolder);
		
		Iterator<File> foundationJars = files().iterate(libFolder, new String[] { "jar" }, true);
		while (foundationJars.hasNext()) {
			classpath.add(foundationJars.next());
		}
		return toFileArray(classpath);
	}

	private Files files() {
		return my(IO.class).files();
	}

	private File foundationSrcFolder() {
		return new File(my(FolderConfig.class).platformSrcFolder().get(), "sneer/foundation");
	}

	private void copyFolder(File srcFolder, File destinationFolder) throws IOException {
		files().copyFolder(srcFolder, destinationFolder);
	}

	private File cleanTmpBrickImplFolder() throws IOException {
		File tmpFolder = new File(my(FolderConfig.class).tmpFolderFor(BrickCompiler.class), "brickimpls");
		resetFolder(tmpFolder);
		return tmpFolder;
	}

	private void resetFolder(File tmpFolder) throws IOException {
		files().forceDelete(tmpFolder);
		tmpFolder.mkdirs();
	}

	private void compileBrick(File brickFolder, File tmpFolder, File[] testsClasspath, File[] implClasspath) throws IOException {
		compile(new File(brickFolder, "tests"), tmpFolder, testsClasspath);
		compile(new File(brickFolder, "impl"), tmpFolder, implClasspath);
	}

	private void compile(File srcFolder, File destinationFolder, File... classpath) throws IOException {
		if (!srcFolder.exists()) return;
		try {
			my(JavaCompiler.class).compile(srcFolder, destinationFolder, classpath);
		} catch (JavaCompilerException e) {
			throw new BrickCompilerException(e);
		}
	}

	private void compileApi(Collection<File> apiFiles, File destinationFolder) throws IOException {
		try {
			my(JavaCompiler.class).compile(apiFiles, destinationFolder, destinationFolder);
		} catch (JavaCompilerException e) {
			throw new BrickCompilerException(e);
		}
	}

	private Collection<File> brickApiFilesIn(File srcFolder) {
		return listJavaFiles(
					srcFolder,
					fileFilters().not(fileFilters().or(
							fileFilters().name("impl"),
							fileFilters().name("tests"))));
	}

	private Collection<File> listJavaFiles(File srcFolder, Filter folderFilter) {
		return fileFilters().listFiles(
				srcFolder,
				fileFilters().suffix(".java"),
				folderFilter);
	}

	protected boolean hasFiles(File folder) {
		for (File file : folder.listFiles())
			if (!file.isDirectory()) return true;
		return false;
	}

	private FileFilters fileFilters() {
		return my(IO.class).fileFilters();
	}

}
