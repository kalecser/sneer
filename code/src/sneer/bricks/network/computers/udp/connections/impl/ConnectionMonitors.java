package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;

class ConnectionMonitors {
	
	private static final ConnectionMonitor[] EMPTY_ARRAY = new ConnectionMonitor[0];
	
	private static final List<ConnectionMonitor> monitors = new ArrayList<ConnectionMonitor>();

	@SuppressWarnings("unused") private static final WeakContract refToAvoidGC = my(Timer.class).wakeUpEvery(UdpConnectionManager.KEEP_ALIVE_PERIOD, new Runnable() { @Override public void run() {
		keepAlive();
	}});

	static void startMonitoring(UdpByteConnection connection) {
		monitors.add(new ConnectionMonitor(connection));
	}

	private static void keepAlive() {
		for (ConnectionMonitor monitor: monitors.toArray(EMPTY_ARRAY))
			monitor.keepAlive();
	}
}
