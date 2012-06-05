package sneer.bricks.network.computers.udp.holepuncher.client.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collection;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;
import sneer.bricks.network.social.attributes.Attributes;
import basis.lang.Consumer;

class StunClientImpl implements StunClient {

	@Override
	public void initSender(Consumer<DatagramPacket> sender) {
		InetAddress serverAddress = my(StunServer.class).inetAddress();
		if (serverAddress == null) return;
		
		StunRequest request = new StunRequest(ownSeal(), new byte[][]{}, localAddressesData());
		byte[] requestBytes = new byte[1024];
		int requestLength = my(StunProtocol.class).marshalRequestTo(request, requestBytes);
		
		DatagramPacket packet = new DatagramPacket(requestBytes, requestLength);		
		packet.setAddress(serverAddress);
		packet.setPort(7777);
		sender.consume(packet);
	}

	private int ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class).currentValue();
	}

	private byte[] localAddressesData() {
		Collection<InetAddress> ownIps = my(OwnIps.class).get().currentElements();
		ByteBuffer buf = ByteBuffer.allocate(2 + 1 + (ownIps.size() * 4)); //Port + Length + ips * 4
		
		buf.putChar((char) ownPort());
		buf.put((byte) ownIps.size());
		
		for (InetAddress inetAddress : ownIps)
			buf.put(inetAddress.getAddress());
		
		return buf.array();
	}

	private byte[] ownSeal() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

	@Override
	public void handle(ByteBuffer stunPacket) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}
}
