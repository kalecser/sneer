package sneer.bricks.network.computers.udp.holepuncher.client.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.own.OwnIps;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import basis.lang.Closure;
import basis.lang.Consumer;

class StunClientImpl implements StunClient {
	
	private static final byte[][] EMPTY_ARRAY = new byte[][]{};

	private Consumer<DatagramPacket> sender;
	
	@SuppressWarnings("unused") private final Object ref1 =
	my(OwnIps.class).get().addPulseReceiver(new Closure() { @Override public void run() {
		sendRequest();
	}});
	@SuppressWarnings("unused") private final Object ref2 =
	my(Timer.class).wakeUpEvery(StunClient.REQUEST_PERIOD, new Closure() {  @Override public void run() {
		sendRequest();
	}});

	
	@Override
	public void initSender(Consumer<DatagramPacket> sender) {
		if (this.sender != null) throw new IllegalStateException();
		this.sender = sender;
		sendRequest();
	}

	
	private void sendRequest() {
		if (sender == null) return;
		
		SocketAddress serverAddress = my(StunProtocol.class).serverAddress();
		if (serverAddress == null) return;
		
		StunRequest request = new StunRequest(ownSeal(), peersToFind(), localAddressesData());
		my(Logger.class).log("Sending Stun Request ", request);
		ByteBuffer buf = ByteBuffer.wrap(new byte[UdpNetwork.MAX_PACKET_PAYLOAD_SIZE]);
		my(StunProtocol.class).marshalRequestTo(request, buf);
		
		DatagramPacket packet = new DatagramPacket(buf.array(), buf.position());		
		packet.setSocketAddress(serverAddress);
		sender.consume(packet);
	}

	
	private byte[][] peersToFind() {
		Contact[] contacts = my(Contacts.class).contacts().currentElements().toArray(new Contact[0]);
		List<byte[]> ret = new ArrayList<byte[]>(contacts.length);
		for (Contact c : contacts) {
			Seal seal = my(ContactSeals.class).sealGiven(c).currentValue();
			if (seal == null) continue;
			ret.add(seal.bytes.copy());
		}
		return ret.toArray(EMPTY_ARRAY);
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
	public void handle(ByteBuffer replyPacket) {
		StunReply reply = my(StunProtocol.class).unmarshalReply(replyPacket);
		
		Contact contact = my(ContactSeals.class).contactGiven(new Seal(reply.peerSeal));
		if (contact == null) return;
		
		ByteBuffer buf = ByteBuffer.wrap(reply.peerLocalAddressData);
		int localPort = buf.getChar(); 
		byte ipsLength = buf.get();
		
		for (int i = 0; i < ipsLength; i++) {
			InetAddress addr = ip(getNextArray(buf, 4));
			my(SightingKeeper.class).keep(contact, new InetSocketAddress(addr, localPort));
		}
	}
	
	
	private static InetAddress ip(byte[] bytes) {
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	private static byte[] getNextArray(ByteBuffer in, int length) {
		if (!in.hasRemaining()) return null;
		byte[] ret = new byte[length];
		in.get(ret);
		return ret;
	}
	
}
