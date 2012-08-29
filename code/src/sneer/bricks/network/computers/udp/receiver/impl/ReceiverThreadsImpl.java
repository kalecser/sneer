package sneer.bricks.network.computers.udp.receiver.impl;

import java.net.DatagramPacket;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import basis.lang.Consumer;


class ReceiverThreadsImpl implements ReceiverThreads {

	@Override
	public Contract start(String threadName, UdpSocket socket, Consumer<DatagramPacket> receiver) {
		return new ReceiverThread(threadName, socket, receiver);
	}

}
