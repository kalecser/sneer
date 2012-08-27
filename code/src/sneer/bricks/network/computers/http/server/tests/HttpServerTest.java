package sneer.bricks.network.computers.http.server.tests;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HttpServerTest {

	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1";
		int port = 8082;
		Server server = new Server(port);
		server.setHandler(new AbstractHandler() {  @Override public void handle(String target, Request request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
			ServletOutputStream out = response.getOutputStream();
			out.write("<h1>It works!</h1>".getBytes());
			out.flush();
		}});
		server.start();
		Desktop.getDesktop().browse(new URI("http://" + host + ":" + port));
	}
	
}
