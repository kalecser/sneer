/**
 * 
 */
package sneer.tests.adapters.impl;

import java.net.URL;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network2010;
import sneer.foundation.brickness.impl.EagerClassLoader;
import sneer.tests.adapters.SneerPartyApiClassLoader;
import sneer.tests.adapters.SneerPartyController;

public final class SneerPartyApiClassLoaderImpl extends EagerClassLoader implements SneerPartyApiClassLoader {
	private final String _name;

	public SneerPartyApiClassLoaderImpl(URL[] urls, ClassLoader next,
			String name) {
		super(urls, next);
		_name = name;
	}

	@Override
	protected boolean isEagerToLoad(String className) {
		return !isSharedByAllParties(className);
	}

	private boolean isSharedByAllParties(String className) {
		if (isNetworkClass(className)) return true;
		if (className.equals(Logger.class.getName())) return true;
		if (className.equals(SneerPartyController.class.getName())) return false;
		if (isPublishedByUser(className)) return false;
		return !isSneerBrick(className); //Foundation classes such as Environments and functional tests classes such as SovereignParty must be shared by all SneerParties.
	}

	private boolean isSneerBrick(String className) {
		return className.startsWith("sneer.bricks");
	}

	private boolean isNetworkClass(String className) {
		if (className.equals(Network2010.class.getName())) return true;
		if (className.equals(ByteArrayServerSocket.class.getName())) return true;
		if (className.equals(ByteArraySocket.class.getName())) return true;
		return false;
	}
	
	@Override
	public Class<?> loadUnsharedBrickClass(String brickName) throws ClassNotFoundException {
		return doLoadClass(brickName);
	}

	private boolean isPublishedByUser(String className) {
		return !className.startsWith("sneer");
	}

	@Override
	public String toString() {
		return _name;
	}	
}