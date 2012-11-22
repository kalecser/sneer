package sneer.bricks.software.code.compilers.scala.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scala.collection.mutable.ListBuffer;
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
	public Result compile(Collection<File> sourceFiles, File destination,
			File... classpath) {
		CollectingReporter reporter = new CollectingReporter();

		Settings settings = new Settings();
		settings.outdir().value_$eq(destination.getPath());
		for (File elem : classpath)
			settings.classpath().append(elem.getAbsolutePath());

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

	private scala.collection.immutable.List<String> fileNames(
			Collection<File> sourceFiles) {
		ListBuffer<String> filesNames = new ListBuffer<String>();
		for (File file : sourceFiles) {
			filesNames = filesNames.$plus$eq(file.getAbsolutePath());
		}
		return filesNames.toList();
	}

	private static final class CollectingReporter extends Reporter {

		private List<CompilationError> _errors = new ArrayList<CompilationError>();

		public List<CompilationError> errors() {
			return _errors;
		}

		@Override
		public void info0(final Position pos, final String msg,
				Severity severity, boolean force) {
			_errors.add(new CompilationError() {

				@Override
				public String message() {
					return msg;
				}

				@Override
				public int lineNumber() {
					return pos.line();
				}

				@Override
				public String fileName() {
					SourceFile file = pos.source();
					return file == null ? "" : file.path();
				}
			});
		}

	}

}
