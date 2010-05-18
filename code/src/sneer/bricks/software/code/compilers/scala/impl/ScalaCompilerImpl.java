package sneer.bricks.software.code.compilers.scala.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scala.Function1;
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;
import scala.tools.nsc.reporters.Reporter;
import scala.tools.nsc.util.Position;
import scala.tools.nsc.util.SourceFile;
import sneer.bricks.software.code.compilers.CompilationError;
import sneer.bricks.software.code.compilers.LanguageRegistry;
import sneer.bricks.software.code.compilers.Result;
import sneer.bricks.software.code.compilers.scala.ScalaCompiler;

public class ScalaCompilerImpl implements ScalaCompiler {

	{
		my(LanguageRegistry.class).addLanguage("scala", this);
	}
	
	
	@Override
	public Result compile(Collection<File> sourceFiles, File destination, File... classpath) {
        CollectingReporter reporter = new CollectingReporter();

        Settings settings = new Settings(new NullErrorFunction());
        settings.outdir().value_$eq(destination.getPath());
        settings.deprecation().value_$eq(true);
        settings.unchecked().value_$eq(true);

        Global compiler = new Global(settings, reporter);
        compiler.new Run().compile(fileNames(sourceFiles));
        final List<CompilationError> errors = reporter.errors();
        return new Result() {
			
			@Override
			public boolean success() {
				return errors.isEmpty();
			}
			
			@Override
			public List<CompilationError> errors() {
				return errors;
			}
			
			@Override
			public String errorString() {
				return errors.toString();
			}
		};
	}

	private scala.List<String> fileNames(Collection<File> sourceFiles) {
		ArrayList<String> fileNames = new ArrayList<String>(sourceFiles.size());
		for (File file : sourceFiles) {
			fileNames.add(file.getAbsolutePath());
		}
		return scala.List$.MODULE$.apply(new scala.collection.jcl.ArrayList<String>(fileNames));
	} 	

	private static final class CollectingReporter extends Reporter {

		private List<CompilationError> _errors = new ArrayList<CompilationError>();
		
		public List<CompilationError> errors() {
			return _errors;
		}
		
		@Override
		public void info0(final Position pos, final String msg, Severity severity, boolean force) {
			_errors.add(new CompilationError() {
				
				@Override
				public String message() {
					return msg;
				}
				
				@Override
				public int lineNumber() {
					return pos.line().get(-1);
				}
				
				@Override
				public String fileName() {
					SourceFile file = pos.source().get(null);
					return file == null ? "" : file.path();
				}
			});
		}
		
	}
	
	private static final class NullErrorFunction implements
			Function1<String, Object> {
		@Override
		public <A> Function1<String, A> andThen(Function1<Object, A> arg0) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
		}

		@Override
		public Object apply(String arg0) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
		}

		@Override
		public <A> Function1<A, Object> compose(Function1<A, String> arg0) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
		}

		@Override
		public int $tag() {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
		}
	}

}

