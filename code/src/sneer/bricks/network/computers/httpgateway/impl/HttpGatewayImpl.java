package sneer.bricks.network.computers.httpgateway.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.URL;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.network.computers.httpgateway.HttpGateway;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

public class HttpGatewayImpl implements HttpGateway {

	@Override
	public void get(final String httpUrl, final Consumer<byte[]> client, final Consumer<IOException> exceptionHandler) {
		my(Threads.class).startDaemon(httpUrl, new Closure(){ @Override public void run() {
				try {
					client.consume(getResponse(httpUrl));
				} catch (IOException e) {
					exceptionHandler.consume(e);
				}
		}});
	}

	
	@Override
	public void get(final String httpUrl, Consumer<byte[]> client) {
		get(httpUrl, client, new Consumer<IOException>(){ @Override public void consume(IOException exception) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Http request failed: " + httpUrl, "", exception, 15000);
		}});
	}

	
	private byte[] getResponse(final String httpUrl) throws IOException {
		return my(IO.class).streams().readBytesAndClose(new URL(httpUrl).openStream());
	}
}