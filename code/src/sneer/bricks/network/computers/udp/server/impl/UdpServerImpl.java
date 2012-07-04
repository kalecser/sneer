package sneer.bricks.network.computers.udp.server.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import sneer.bricks.network.computers.udp.sender.UdpSender;
import sneer.bricks.network.computers.udp.server.UdpServer;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.Crashed;


public class UdpServerImpl implements UdpServer, Consumer<DatagramPacket> {

	private static final long RETRY_PERIOD = 10 * 1000;
	private final Light sendError = my(BlinkingLights.class).prepare(LightType.ERROR);
	private final Light openError = my(BlinkingLights.class).prepare(LightType.ERROR);
	private UdpSocket socket;
	private Contract receiverThread;
	private WeakContract retryContract;
	
	
	@SuppressWarnings("unused") private WeakContract refToAvoidGC = 
	ownPort().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
		handlePort(port);
	}});


	static private Signal<Integer> ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class);
	}
	
	
	{
		my(UdpSender.class).init(new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
			send(packet);
		}});
	}
	
	
	synchronized
	private void handlePort(int port) {
		crash();
		if (port < 1) return; 
		open(port);
	}


	synchronized
	private void open(int port) {
		socket = tryToOpenSocket(port);
		if(socket == null) return;
		receiverThread = my(ReceiverThreads.class).start(socket, this);
	}

	
	private UdpSocket tryToOpenSocket(int port) {
		try {
			UdpSocket ret = my(UdpNetwork.class).openSocket(port);
			crashRetryIfNecessary();
			my(BlinkingLights.class).turnOffIfNecessary(openError);
			return ret;
		} catch (SocketException e) {
			my(BlinkingLights.class).turnOnIfNecessary(openError, "Network Error", "Unable to open UDP server on port " + port, e);
			retryLater();
			return null;
		}
	}
	
	
	private void retryLater() {
		retryContract = my(Timer.class).wakeUpInAtLeast(RETRY_PERIOD, new Closure() { @Override public void run() {
			open(ownPort().currentValue());
		}});
	}


	private void send(DatagramPacket packet) {
		if (socket == null) return;
		try {
			socket.send(packet);
			my(BlinkingLights.class).turnOffIfNecessary(sendError);
		} catch (Crashed e) {
			//Crashed in test mode.
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(sendError, "Error sending UDP packet", e);
		}
	}
	
	
	@Override
	public void consume(DatagramPacket packet) {
		my(UdpConnectionManager.class).handle(packet);
	}

	
	@Override
	public void crash() {
		crashRetryIfNecessary();
		
		if (receiverThread != null) {
			receiverThread.dispose();
			receiverThread = null;
		}
		
		if (socket != null) {
			socket.crash();
			socket = null;
		}
	}


	private void crashRetryIfNecessary() {
		if (retryContract == null) return; 
		retryContract.dispose();
		retryContract = null;
	}

}
