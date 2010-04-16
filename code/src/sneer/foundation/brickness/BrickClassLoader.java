package sneer.foundation.brickness;

public interface BrickClassLoader {
	
	public enum Kind {
		IMPL,
		LIBS
	}

	String brickName();

	Kind kind();

}
