package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import basis.lang.Closure;
import basis.lang.Consumer;


class UdpPortSession {

	private static final long RETRY_PERIOD = 3 * 1000;
	
	private UdpSocketHolder socketHolder;
	private WeakContract retryContract;
	private final int port;
	private final Consumer<DatagramPacket> receiver;
	
	
	UdpPortSession(int port, Consumer<DatagramPacket> receiver) {
		this.port = port;
		this.receiver = receiver;
		retryContract = my(Timer.class).wakeUpNowAndEvery(RETRY_PERIOD, new Closure() { @Override public void run() {
			retryUntilOpen();
		}});
	}


	synchronized
	private void retryUntilOpen() {
		socketHolder = UdpSocketHolder.newHolderFor(port, receiver);
		if (socketHolder == null) return;
		retryContract.dispose();
	}

	
	void send(DatagramPacket packet) {
		if (socketHolder == null) return;
		socketHolder.send(packet);
	}
	
	
	void crash() {
		if (retryContract != null) retryContract.dispose();
		if (socketHolder != null) socketHolder.crash();
	}

}
