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
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.Builder;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.java.JavaCompilerException;
import sneer.bricks.software.folderconfig.FolderConfig;

class Build {

	private final File _srcFolder;
	private final File _destFolder;


	Build(File srcFolder, File destFolder) throws IOException {
		_srcFolder = srcFolder;
		_destFolder = destFolder;
		
		buildFoundation();
		buildBricks();
		copyResources();
	}

	
	private void buildFoundation() throws IOException {
		File foundationSrc = foundationSrcFolder();
		File[] fileArray = toFileArray(jarsIn(foundationSrc));
		compile(foundationSrc, _destFolder, fileArray);
	}


	private File foundationSrcFolder() {
		return new File(_srcFolder, "sneer/foundation");
	}
	
	
	private void buildBricks() throws IOException {
		Collection<File> apiFiles = brickApiFiles();
		if (apiFiles.isEmpty()) return;
		
		compileApi(apiFiles, _destFolder);
		compileBricks(parentFolders(apiFiles));
	}

	
	private void copyResources() throws IOException {
		my(Logger.class).log("Copying resources...");
		Filter nonJavaFiles = fileFilters().not(fileFilters().suffix(".java"));
		files().copyFolder(_srcFolder, _destFolder, nonJavaFiles);
		my(Logger.class).log("Copying resources... done.");

	}

	
	private Collection<File> parentFolders(Collection<File> files) {
		HashSet<File> result = new HashSet<File>();
		for (File file : files)
			result.add(file.getParentFile());
		return result;
	}
	
	
	private void compileBricks(Collection<File> brickFolders) throws IOException {
		File tmpFolder = cleanTmpBrickImplFolder();
		
//		File[] testClasspath = testClassPath();
		
		int i = 0;
		for (File brickFolder : brickFolders) {
			my(Logger.class).log("Compiling brick {} of ", ++i, brickFolders.size());
			File[] implClasspath = classpathWithLibs(new File(brickFolder, "impl/lib"));
			compileBrick(brickFolder, tmpFolder, implClasspath);
		}
		
		my(Logger.class).log("Copying compiled bricks...");
		copyFolder(tmpFolder, _destFolder); //Refactor: This whole thing of compiling to a tmpFolder so that one brick cannot reference the impl of another directly will no longer be necessary once the check that no class can be public inside an impl is done elsewhere.   
		my(Logger.class).log("Copying compiled bricks... done.");
	}

	
	private File[] toFileArray(List<File> testClasspath) {
		return testClasspath.toArray(new File[testClasspath.size()]);
	}

	
	@SuppressWarnings("unused")
	private File[] testClassPath() {
		return classpathWithLibs(foundationSrcFolder());
	}

	
	private File[] classpathWithLibs(File libFolder) {
		if (!libFolder.exists()) {
			return new File[] { _destFolder };
		}
		
		List<File> classpath = jarsIn(libFolder);
		classpath.add(_destFolder);
		return toFileArray(classpath);
	}

	
	private List<File> jarsIn(File libFolder) {
		List<File> classpath = new ArrayList<File>();
		Iterator<File> foundationJars = files().iterate(libFolder, new String[] { "jar" }, true);
		while (foundationJars.hasNext()) {
			classpath.add(foundationJars.next());
		}
		return classpath;
	}

	
	private Files files() {
		return my(IO.class).files();
	}

	
	private void copyFolder(File from, File to) throws IOException {
		files().copyFolder(from, to);
	}

	
	private File cleanTmpBrickImplFolder() throws IOException {
		File tmpFolder = new File(my(FolderConfig.class).tmpFolderFor(Builder.class), "brickimpls");
		resetFolder(tmpFolder);
		return tmpFolder;
	}

	
	private void resetFolder(File tmpFolder) throws IOException {
		files().forceDelete(tmpFolder);
		tmpFolder.mkdirs();
	}

	
	private void compileBrick(File brickFolder, File tmpFolder, File[] implClasspath) throws IOException {
		compile(new File(brickFolder, "impl"), tmpFolder, implClasspath);
	}

	
	private void compile(File srcFolder, File binFolder, File... classpath) throws IOException {
		if (!srcFolder.exists()) return;
		try {
			my(JavaCompiler.class).compile(srcFolder, binFolder, classpath);
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

	
	private Collection<File> brickApiFiles() {
		return listJavaFiles(
			fileFilters().not(fileFilters().or(
				fileFilters().name("impl"),
				fileFilters().name("tests"),
				fileFilters().name("foundation"))));
	}

	
	private Collection<File> listJavaFiles(Filter folderFilter) {
		return fileFilters().listFiles(
			_srcFolder,
			fileFilters().suffix(".java"),
			folderFilter);
	}

	
//	private boolean hasFiles(File folder) {
//		for (File file : folder.listFiles())
//			if (!file.isDirectory()) return true;
//		return false;
//	}

	
	private FileFilters fileFilters() {
		return my(IO.class).fileFilters();
	}

}
