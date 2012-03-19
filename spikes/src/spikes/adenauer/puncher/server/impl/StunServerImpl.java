package spikes.adenauer.puncher.server.impl;

import java.net.DatagramPacket;

import spikes.adenauer.puncher.server.StunServer;


class StunServerImpl implements StunServer {

	@Override
	public void handleAndUseForReply(DatagramPacket packet) {
		byte[] reply = packet.getAddress().getAddress();
		setData(packet, reply);
	}


	private void setData(DatagramPacket packet, byte[] reply) {
		int length = reply.length;
		System.arraycopy(reply, 0, packet.getData(), 0, length);
		packet.setLength(length);
	}

}
