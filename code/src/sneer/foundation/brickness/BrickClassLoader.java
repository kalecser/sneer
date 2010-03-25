package sneer.foundation.brickness;

public interface BrickClassLoader {
	
	public enum Kind {
		IMPL,
		LIBS
	}

	Class<?> brick();

	Kind kind();

}
