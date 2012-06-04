package sneer.bricks.network.computers.udp.holepuncher.client.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.holepuncher.server.impl.StunRequest;
import sneer.bricks.network.social.attributes.Attributes;
import basis.lang.Consumer;

class StunClientImpl implements StunClient {

	@Override
	public void initSender(Consumer<DatagramPacket> sender) {
		StunRequest request = new StunRequest(ownSeal(), ownIp(), ownPort(), null);
		byte[] requestBytes = new byte[1024];
		int requestLength = request.marshalTo(requestBytes);
		DatagramPacket packet = new DatagramPacket(requestBytes, requestLength);
		try {
			packet.setAddress(InetAddress.getByName("dynamic.sneer.me"));
		} catch (UnknownHostException e) {
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
		packet.setPort(7777);
		sender.consume(packet);
	}

	private int ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class).currentValue();
	}

	private InetAddress ownIp() {
		Iterator<InetAddress> it = my(OwnIps.class).get().currentElements().iterator();
		return it.hasNext() ? it.next() : null;
	}

	private byte[] ownSeal() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

	@Override
	public void handle(ByteBuffer stunPacket) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}
}
