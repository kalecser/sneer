package sneer.bricks.software.code.compilers.java.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.bricks.software.code.compilers.java.JavaCompilerException;
import sneer.bricks.software.code.compilers.java.Result;
import sneer.foundation.lang.Functor;

import com.sun.tools.javac.Main;

class JavaCompilerImpl implements JavaCompiler {

	@Override
	public void compile(File srcFolder, File destinationFolder,	File... classpath) throws JavaCompilerException, IOException {
		List<File> srcFiles = new ArrayList<File>(my(IO.class).files().listFiles(srcFolder, new String[]{"java"}, true));

		compile(srcFiles, destinationFolder, classpath);
	}

	@Override
	public Result compile(Collection<File> sourceFiles, File destination, File... classpath) throws IOException, JavaCompilerException {
		
		File tmpFile = createArgsFileForJavac(sourceFiles);
		my(Logger.class).log("Compiling {} files to {}", sourceFiles.size(), destination);

		String[] parameters = {
				"-classpath", asJavacArgument(classpath),
				"-d", destination.getAbsolutePath(),
				"-encoding","UTF-8",
				"@"+tmpFile.getAbsolutePath()
		};
		my(Logger.class).log("Compiler command line: ", my(Lang.class).strings().join(Arrays.asList(parameters), " "));

		StringWriter writer = new StringWriter();
		int code = Main.compile(parameters, new PrintWriter(writer));
		tmpFile.delete();
		Result result = new CompilationResult(code, destination);
		if (code != 0) {
			result.setError(writer.getBuffer().toString());
			throw new JavaCompilerException(result);
		}
		return result;
	}

	
	private String asJavacArgument(File[] classpath) {
		Collection<String> result = my(CollectionUtils.class).map(Arrays.asList(classpath), new Functor<File, String>() { @Override public String evaluate(File entry) {
			return entry.isDirectory()
				? entry.getAbsolutePath() + "/"    //Refactor: Is adding this slash really necessary?
				: entry.getAbsolutePath();
		}});
	
		return my(Lang.class).strings().join(result, File.pathSeparator);
	}

	private File createArgsFileForJavac(Collection<File> files) throws IOException {
		File args = File.createTempFile("javac-", ".args");
			
		my(IO.class).files().writeString(args, my(Lang.class).strings().join(files, "\n"));
		return args;
	}

//	private List<File> buildSourceList(File source) {
//		JavaDirectoryWalker walker = new JavaDirectoryWalker(source);
//		List<File> files;
//		try {
//			files = walker.list();
//		} catch (IOException e) {
//			throw new CompilerException("Error building source list", e);
//		}
//		return files;
//	}

//	private String buildClassPath(File libDir) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(System.getProperty("java.home")).append(File.separator).append("lib").append(File.separator).append("rt.jar");
//		if(!sneer.lego.utils.FileUtils.isEmpty(libDir)) {
//			sb.append(File.pathSeparatorChar);
//			File[] libs = libDir.listFiles((FilenameFilter) new SuffixFileFilter(".jar"));
//			for (File lib : libs) {
//				sb.append(lib.getAbsolutePath());
//				sb.append(File.pathSeparatorChar);
//			}
//		}
//		String classPath = sb.toString(); 
//		return classPath;
//	}
}