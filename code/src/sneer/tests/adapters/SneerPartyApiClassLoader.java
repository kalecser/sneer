package sneer.tests.adapters;


public interface SneerPartyApiClassLoader {

	Class<?> loadUnsharedBrickClass(String brickName) throws ClassNotFoundException;

}
