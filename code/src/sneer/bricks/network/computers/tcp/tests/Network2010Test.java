package sneer.bricks.network.computers.tcp.tests;

import static basis.environments.Environments.my;

import java.io.IOException;

import org.junit.Test;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.tcp.ByteArrayServerSocket;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.network.computers.tcp.TcpNetwork;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class Network2010Test extends BrickTestBase {
	
	private final Threads _threads = my(Threads.class);
	
	@Test (timeout = 2000)
	public void sameMachine() throws Exception {
		TcpNetwork network = my(TcpNetwork.class);
		final ByteArrayServerSocket server = network.openServerSocket(9090);

		_threads.startDaemon("Uppercase Service", new Closure() { @Override public void run() {
			try {
				ByteArraySocket request = server.accept();
				request.write(new String(request.read()).toUpperCase().getBytes("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}});

		ByteArraySocket client = network.openSocket("localhost", 9090);
		client.write("hello".getBytes("UTF-8"));
		byte[] reply = client.read();
		assertEquals("HELLO", new String(reply));
		
		server.crash();
	}
}