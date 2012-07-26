package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Closure;
import basis.lang.Consumer;


class UdpPortSession {

	private static final long THREE_SECONDS = 3 * 1000;
	private static final Light openError = my(BlinkingLights.class).prepare(LightType.ERROR);

	private final int port;
	private final Consumer<DatagramPacket> receiver;
	private UdpSocketHolder socketHolder;
	private WeakContract retryContract;
	
	
	UdpPortSession(int port, Consumer<DatagramPacket> receiver) {
		this.port = port;
		this.receiver = receiver;
		retryContract = my(Timer.class).wakeUpNowAndEvery(THREE_SECONDS, new Closure() { @Override public void run() {
			if (tryToOpenSocket());
				retryContract.dispose();
		}});
	}


	private boolean tryToOpenSocket() {
		my(BlinkingLights.class).turnOffIfNecessary(openError);
		try {
			socketHolder = new UdpSocketHolder(port, receiver);
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOnIfNecessary(openError, "Network Error", "Unable to open UDP socket on port " + port + " (trying every few seconds)...", e);
			return false;
		}
		return true;
	}

	
	void send(DatagramPacket packet) {
		if (socketHolder == null) return;
		socketHolder.send(packet);
	}
	
	
	void crash() {
		my(BlinkingLights.class).turnOffIfNecessary(openError);
		if (retryContract != null) retryContract.dispose();
		if (socketHolder != null) socketHolder.crash();
	}

}
