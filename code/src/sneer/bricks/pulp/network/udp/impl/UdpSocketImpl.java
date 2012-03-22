package sneer.bricks.pulp.network.udp.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.network.udp.UdpNetwork;
import sneer.bricks.pulp.network.udp.UdpNetwork.UdpSocket;


class UdpSocketImpl implements UdpSocket {

	private final DatagramSocket socket;

	private Consumer<DatagramPacket> receiver;
	private final DatagramPacket incoming = initIncomingPacket();
	private final Light errorReceiving = my(BlinkingLights.class).prepare(LightType.ERROR);
	private Contract receptionContract;



	UdpSocketImpl(int portNumber) throws SocketException {
		socket = new DatagramSocket(portNumber);
	}

	
	@Override
	public void initReceiver(Consumer<DatagramPacket> receiver) {
		if (this.receiver != null) throw new IllegalStateException();
		this.receiver = receiver; 
		receptionContract = my(Threads.class).startStepping(new Closure() { @Override public void run() {
			receivePacket();
		}});
	}


	@Override
	public void send(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}

	
	private DatagramPacket initIncomingPacket() {
		byte[] buffer = new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE];
		return new DatagramPacket(buffer, buffer.length);
	}


	private void receivePacket() {
		try {
			tryToReceivePacket();
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(errorReceiving, "Error receiving UDP Packet", e);
			receptionContract.dispose();
		}
	}


	private void tryToReceivePacket() throws IOException {
		incoming.setLength(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		socket.receive(incoming);
		receiver.consume(incoming);
	}


	@Override
	public void crash() {
		socket.close();
	}

}
