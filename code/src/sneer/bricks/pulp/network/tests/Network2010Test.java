package sneer.bricks.pulp.network.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network2010;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Closure;


public class Network2010Test extends BrickTestBase {
	
	private final Threads _threads = my(Threads.class);
	
	@Test(timeout = 2000)
	public void testNetworkMessages() throws Exception {
		
		final Network2010 network = my(Network2010.class);
		
		final ByteArrayServerSocket server = network.openServerSocket(9090);

		_threads.startDaemon("Network Test", new Closure() { @Override public void run() {
			try {
				ByteArraySocket request = server.accept();
				request.write(new String(request.read()).toUpperCase().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}});

		ByteArraySocket client = network.openSocket("localhost", 9090);
		client.write("hello".getBytes());
		byte[] reply = client.read();
		assertEquals("HELLO", new String(reply));
	}
}