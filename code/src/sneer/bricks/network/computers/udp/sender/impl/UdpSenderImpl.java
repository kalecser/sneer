package sneer.bricks.network.computers.udp.sender.impl;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.udp.sender.UdpSender;
import basis.lang.Consumer;

class UdpSenderImpl implements UdpSender {

	private Consumer<DatagramPacket> sender;

	@Override
	public void send(DatagramPacket packet) {
		if (sender == null) return;
		sender.consume(packet);
	}

	@Override
	public void init(Consumer<DatagramPacket> sender) {
		if (this.sender != null) throw new IllegalStateException();
		this.sender = sender;
	}

}
