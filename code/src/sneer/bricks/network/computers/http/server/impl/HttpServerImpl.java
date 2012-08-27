package sneer.bricks.network.computers.http.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import sneer.bricks.hardware.cpu.lang.contracts.Contracts;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;

public class HttpServerImpl implements HttpServer {

	@Override
	public WeakContract start(int port, final HttpHandler httpHandler) throws IOException {
		System.setProperty("org.eclipse.jetty.LEVEL", "WARN");	
		Server server = new Server(port);
		server.setHandler(wrapped(httpHandler));
		start(port, server);
		return weakContractFor(server);
	}

	private WeakContract weakContractFor(final Server server) {
		return my(Contracts.class).weakContractFor(new Disposable() { @Override public void dispose() {
			try {
				server.stop();
			} catch (Exception e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error stopping webserver", "Get a sovereign friend to help you.", e, 10000);
			}
		}});
	}

	private void start(int port, final Server server) throws IOException {
		try {
			server.start();
		} catch (Exception e) {
			throw new IOException("Exception starting HttpServer on port " + port, e);
		}
	}

	private AbstractHandler wrapped(final HttpHandler httpHandler) {
		return new AbstractHandler() { @Override public void handle(String target, Request request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
			ServletOutputStream out = response.getOutputStream();
			String encoding = request.getCharacterEncoding();
			out.write(httpHandler.replyFor(target).getBytes(encoding == null ? "UTF-8" : encoding));
			out.flush();
		}};
	}

}
