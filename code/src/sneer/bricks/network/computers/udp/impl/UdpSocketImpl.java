package sneer.bricks.network.computers.udp.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Closure;
import basis.lang.Consumer;


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
		incoming.setLength(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		try {
			socket.receive(incoming);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(errorReceiving, "Error receiving UDP Packet", e);
			receptionContract.dispose();
			return;
		}
		receiver.consume(incoming);
	}


	@Override
	public void crash() {
		socket.close();
	}

}
