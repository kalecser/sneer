package sneer.bricks.pulp.serialization;


public interface ClassMapper {

	String serializationHandleFor(Class<?> klass);
	
	Class<?> classGiven(String serializationHandle) throws ClassNotFoundException;
}
