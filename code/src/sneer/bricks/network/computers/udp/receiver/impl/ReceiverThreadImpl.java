package sneer.bricks.network.computers.udp.receiver.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.receiver.ReceiverThread;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.Crashed;

class ReceiverThreadImpl implements ReceiverThread {

	private final UdpSocket socket;
	private final Consumer<DatagramPacket> receiver;
	
	private final DatagramPacket incoming = initIncomingPacket();
	private final Light error = my(BlinkingLights.class).prepare(LightType.ERROR);
	private Contract receptionContract;


	ReceiverThreadImpl(UdpSocket socket, Consumer<DatagramPacket> receiver) {
		this.socket = socket;
		this.receiver = receiver;
	
		receptionContract = my(Threads.class).startStepping(new Closure() { @Override public void run() {
			receivePacket();
		}});
	}


	private void receivePacket() {
		incoming.setLength(UdpNetwork.MAX_PACKET_PAYLOAD_SIZE);
		try {
			socket.receive(incoming);
		} catch (Crashed e) {
			receptionContract.dispose();
			return;
		} catch (IOException e) {
			if (receptionContract == null) return;
			my(BlinkingLights.class).turnOnIfNecessary(error, "Error receiving UDP Packet", e);
			receptionContract.dispose();
			return;
		}
		receiver.consume(incoming);
	}


	private static DatagramPacket initIncomingPacket() {
		byte[] buffer = new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE];
		return new DatagramPacket(buffer, buffer.length);
	}


	@Override
	public void crash() {
		receptionContract.dispose();
		receptionContract = null;
	}

}
