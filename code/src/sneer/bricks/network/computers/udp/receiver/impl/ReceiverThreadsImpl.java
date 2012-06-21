package sneer.bricks.network.computers.udp.receiver.impl;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.receiver.ReceiverThreads;
import basis.lang.Consumer;


class ReceiverThreadsImpl implements ReceiverThreads {

	@Override
	public void start(UdpSocket socket, Consumer<DatagramPacket> receiver) {
		new ReceiverThreadImpl(socket, receiver);
	}

}
