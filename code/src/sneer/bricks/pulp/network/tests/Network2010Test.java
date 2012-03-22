package sneer.bricks.pulp.network.tests;

import static basis.environments.Environments.my;

import java.io.IOException;

import org.junit.Test;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network2010;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class Network2010Test extends BrickTestBase {
	
	private final Threads _threads = my(Threads.class);
	
	@Test (timeout = 2000)
	public void sameMachine() throws Exception {
		Network2010 network = my(Network2010.class);
		final ByteArrayServerSocket server = network.openServerSocket(9090);

		_threads.startDaemon("Uppercase Service", new Closure() { @Override public void run() {
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
		
		server.crash();
	}
}