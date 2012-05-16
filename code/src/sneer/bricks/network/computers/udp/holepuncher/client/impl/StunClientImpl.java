package sneer.bricks.network.computers.udp.holepuncher.client.impl;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import basis.lang.Consumer;

class StunClientImpl implements StunClient {

	@Override
	public void initSender(Consumer<DatagramPacket> sender) {
//		StunRequest request = new StunRequest(ownSeal(), InetAddress.getLocalHost().getHostAddress(), localPort, peerToFind);
//		byte[] requestBytes = new byte[1024];
//		int requestLength = request.marshalTo(requestBytes );
//		DatagramPacket packet = new DatagramPacket(requestBytes, requestLength );
//		sender.consume(packet );
	}

//	private byte[] ownSeal() {
//		return my(OwnSeal.class).get().currentValue().bytes.copy();
//	}
}
