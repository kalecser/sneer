package sneer.bricks.software.code.compilers;


public interface Language {

	String fileExtension();

	LanguageCompiler compiler();

}
