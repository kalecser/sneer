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
import sneer.bricks.software.code.compilers.CompilerException;
import sneer.bricks.software.code.compilers.Language;
import sneer.bricks.software.code.compilers.Result;
import sneer.bricks.software.folderconfig.FolderConfig;

class Build {

	private final File _srcFolder;
	private final File _destFolder;
	private final Language _language;


	Build(File srcFolder, File destFolder, Language language) throws IOException, BrickCompilerException {
		_srcFolder = srcFolder;
		_destFolder = destFolder;
		_language = language;
		
		buildFoundation();
		buildBricks();
		copyResources();
	}

	
	private void buildFoundation() throws IOException, BrickCompilerException {
		compile(foundationSrcFolder(), _destFolder);
	}


	private File foundationSrcFolder() {
		return new File(_srcFolder, "sneer/foundation");
	}
	
	
	private void buildBricks() throws IOException, BrickCompilerException {
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
	
	
	private void compileBricks(Collection<File> brickFolders) throws IOException, BrickCompilerException {
		File tmpFolder = cleanTmpBrickImplFolder();
		
		compileImplsTo(brickFolders, tmpFolder);
		compileTests(brickFolders);
		
		copyFolder(tmpFolder, _destFolder); //Refactor: This whole thing of compiling to a tmpFolder so that one brick cannot reference the impl of another directly will no longer be necessary once the check that no class can be public inside an impl is done elsewhere.   
	}


	private void compileTests(Collection<File> brickFolders) throws IOException, BrickCompilerException {
		Collection<File> testFiles = new ArrayList<File>();
		for (File brickFolder : brickFolders)
			accumulateTestFiles(testFiles, brickFolder);
		
		if (testFiles.isEmpty()) return;
		compileTestFiles(testFiles);
	}


	private void accumulateTestFiles(Collection<File> testFiles, File brickFolder) {
		accumulateTestFiles(testFiles, brickFolder, "tests");
		accumulateTestFiles(testFiles, brickFolder, "testsupport");
	}


	private void accumulateTestFiles(Collection<File> testFiles,
			File brickFolder, String subfolder) {
		File testsFolder = new File(brickFolder, subfolder);
		if (testsFolder.exists())
			testFiles.addAll(listJavaFiles(testsFolder, fileFilters().any()));
	}

	
	private Result compile(Collection<File> sourceFiles, File destination, File... classpath) throws IOException, CompilerException {
		return _language.compiler().compile(sourceFiles, destination, classpath);
	}

	
	private void compileTestFiles(Collection<File> testFiles) throws IOException, BrickCompilerException {
		try {
			compile(testFiles, _destFolder, classpathForTests());
		} catch (CompilerException e) {
			throw new BrickCompilerException(e);
		}
	}

	
	private void compileImplsTo(Collection<File> brickFolders, File tmpFolder) throws IOException, BrickCompilerException {
		int i = 0;
		for (File brickFolder : brickFolders) {
			my(Logger.class).log("Compiling brick impl {} of ", ++i, brickFolders.size());
			compileBrick(brickFolder, tmpFolder);
		}
	}

	
	private File[] toFileArray(List<File> list) {
		return list.toArray(new File[list.size()]);
	}

	
	private File[] classpathForTests() {
		return classpathWithLibs(foundationSrcFolder());
	}

	
	private File[] classpathWithLibs(File libFolder) {
		if (!libFolder.exists())
			return new File[] { _destFolder };
		
		List<File> result = jarsIn(libFolder);
		result.add(_destFolder);
		return toFileArray(result);
	}

	
	private List<File> jarsIn(File libFolder) {
		List<File> result = new ArrayList<File>();
		boolean recursive = true;
		Iterator<File> jars = files().iterate(libFolder, new String[] { "jar" }, recursive);
		while (jars.hasNext())
			result.add(jars.next());

		return result;
	}

	
	private Files files() {
		return my(IO.class).files();
	}

	
	private void copyFolder(File from, File to) throws IOException {
		files().copyFolder(from, to);
	}

	
	private File cleanTmpBrickImplFolder() throws IOException {
		File result = new File(my(FolderConfig.class).tmpFolderFor(Builder.class), "brickimpls");
		resetFolder(result);
		return result;
	}

	
	private void resetFolder(File tmpFolder) throws IOException {
		files().forceDelete(tmpFolder);
		tmpFolder.mkdirs();
	}

	
	private void compileBrick(File brickFolder, File tmpFolder) throws IOException, BrickCompilerException {
		File[] classpath = classpathWithLibs(new File(brickFolder, "impl/lib"));
		File srcFolder = new File(brickFolder, "impl");
		if (!srcFolder.exists()) return;
		compile(srcFolder, tmpFolder, classpath);
	}

	
	private void compile(File srcFolder, File binFolder, File... classpath) throws IOException, BrickCompilerException {
		try {
			compile(srcFilesIn(srcFolder), binFolder, classpath);
		} catch (CompilerException e) {
			throw new BrickCompilerException(e);
		}
	}


	private ArrayList<File> srcFilesIn(File srcFolder) {
		return new ArrayList<File>(my(IO.class).files().listFiles(srcFolder, new String[]{_language.fileExtension()}, true));
	}

	
	private void compileApi(Collection<File> apiFiles, File destinationFolder) throws IOException, BrickCompilerException {
		try {
			compile(apiFiles, destinationFolder, destinationFolder);
		} catch (CompilerException e) {
			throw new BrickCompilerException(e);
		}
	}

	
	private Collection<File> brickApiFiles() {
		return listJavaFiles(
			_srcFolder,
			fileFilters().not(fileFilters().or(new Filter[] {
				fileFilters().name("impl"),
				fileFilters().name("tests"),
				fileFilters().name("testsupport"),
				fileFilters().name("foundation")
			})));
	}

	
	private Collection<File> listJavaFiles(File srcFolder, Filter filterForSubfolders) {
		return fileFilters().listFiles(
			srcFolder,
			fileFilters().suffix(".java"),
			filterForSubfolders);
	}

	
	private FileFilters fileFilters() {
		return my(IO.class).fileFilters();
	}

}
