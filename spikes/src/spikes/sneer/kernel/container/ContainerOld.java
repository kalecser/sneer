package spikes.sneer.kernel.container;

import basis.environments.Environment;

public interface ContainerOld extends Environment {
	
	Class<?> resolve(String brickName) throws ClassNotFoundException;

}