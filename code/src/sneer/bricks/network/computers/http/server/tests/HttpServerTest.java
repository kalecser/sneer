package sneer.bricks.network.computers.http.server.tests;

import static basis.environments.Environments.my;

import java.io.OutputStream;
import java.net.Socket;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class HttpServerTest extends BrickTestBase{
	
	private static final int HTTP_PORT = 8088;
	private final HttpServer subject = my(HttpServer.class);

	@Test (timeout = 2000)
	public void httpServer() throws Exception{
		WeakContract contract = subject.start(HTTP_PORT, new HttpHandler(){ @Override public String replyFor(String target){
			return "reply for: " + target;
		}});
		
		String actual = replyFor(8088, "/foobar&baz");
		assertTrue(actual.contains("reply for: /foobar&baz"));
		
		contract.dispose();
	}

	public static String replyFor(int port, String target) throws Exception {
		try (Socket socket = new Socket("localhost", port)) {
			OutputStream out = socket.getOutputStream();
			out.write(("GET "+ target + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n").getBytes());
			out.flush();
			return my(IO.class).streams().toString(socket.getInputStream());
		}
	}	
}
