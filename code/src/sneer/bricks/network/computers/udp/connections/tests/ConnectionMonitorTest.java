package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.Test;

import sneer.bricks.network.computers.udp.connections.impl.ConnectionMonitor;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.brickness.testsupport.Bind;

public class ConnectionMonitorTest extends BrickTestBase {
	
	private final SetRegister<SocketAddress> sightings = my(CollectionSignals.class).newSetRegister();
	@SuppressWarnings("unused") private ConnectionMonitor subject = new ConnectionMonitor(sightings.output());
	@Bind private final LoggingSender sender = new LoggingSender();

	@Test (timeout=2000)
	public void onSighting_ShouldHail() throws Exception {
		sightings.add(new InetSocketAddress("200.201.202.203", 123));
		my(SignalUtils.class).waitForValue(sender.history(), "| hail 0,to:200.201.202.203,port:123");
	}

}
