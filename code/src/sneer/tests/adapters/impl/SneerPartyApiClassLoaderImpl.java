/**
 * 
 */
package sneer.tests.adapters.impl;

import java.net.URL;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.tcp.ByteArrayServerSocket;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.network.computers.tcp.TcpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.tests.adapters.SneerPartyApiClassLoader;
import sneer.tests.adapters.SneerPartyController;
import basis.brickness.impl.EagerClassLoader;

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
		if (className.equals(UdpNetwork.class.getName())) return true;
		if (className.equals(UdpNetwork.UdpSocket.class.getName())) return true;

		if (className.equals(TcpNetwork.class.getName())) return true;
		if (className.equals(ByteArrayServerSocket.class.getName())) return true;
		if (className.equals(ByteArraySocket.class.getName())) return true;
		return false;
	}
	
	@Override
	public Class<?> loadUnsharedBrickClass(String brickName) throws ClassNotFoundException {
		return doLoadClass(brickName);
	}

	private boolean isPublishedByUser(String className) {
		if (className.startsWith("sneer")) return false;
		if (className.startsWith("basis")) return false;
		return true;
	}

	@Override
	public String toString() {
		return _name;
	}	
}