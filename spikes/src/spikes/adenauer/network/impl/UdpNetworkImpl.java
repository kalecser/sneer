package spikes.adenauer.network.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Map.Entry;
import java.util.Set;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;
import spikes.adenauer.network.UdpNetwork;

public final class UdpNetworkImpl implements UdpNetwork {
	
	private DatagramSocket _udpSocket;

	private final DatagramPacket _packetToReceive = newPacket();
	private final DatagramPacket _packetToSend = newPacket();

	private final EventNotifier<Packet> _notifier = my(EventNotifiers.class).newInstance();
	private final CacheMap<Seal, SocketAddress> _endpoints = CacheMap.newInstance();

	@SuppressWarnings("unused")	private final Object _refToAvoidGc;

	
	UdpNetworkImpl() {
		_refToAvoidGc = ownPort().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
				start(port);
		}});
	}

	
	private void start(Integer port) {
		try {
			_udpSocket = new DatagramSocket(port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		listener(_udpSocket);
	}

	
	private void listener(DatagramSocket udpSocket) {
		while (true) {
			try {
				receivePacketFrom(udpSocket);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	
	private synchronized void receivePacketFrom(DatagramSocket udpSocket) throws IOException  {
		udpSocket.receive(_packetToReceive);
		if (_endpoints.contains( _packetToReceive.getSocketAddress())) {
			Packet packet = newPacket(_packetToReceive); 
			_notifier.notifyReceivers(packet);
		}	
	}

	@Override
	public SetSignal<Seal> peersOnline() {
		return null;
	}

	@Override
	public synchronized void send(byte[] data, Seal destination) throws IOException {
		_packetToSend.setData(data);
		_packetToSend.setSocketAddress(_endpoints.get(destination));
		_udpSocket.send(_packetToSend);
	}

	
	@Override
	public EventSource<Packet> packetsReceived() {
		return _notifier.output();
	}

	
	private DatagramPacket newPacket() {
		byte[] array = new byte[UdpNetwork.MAX_ARRAY_SIZE];
		return new DatagramPacket(array, array.length);
	}
	
	
	private Packet newPacket(DatagramPacket receivedPacket) {
		byte[] data = receivedPacket.getData();
		Seal sender = findSenderFromEndPoints(receivedPacket.getSocketAddress());
		return new UdpPacket(data, sender);
	}

	
	private Seal findSenderFromEndPoints(SocketAddress packetEndPoint) {
		Seal sender = null;
		Set<Entry<Seal, SocketAddress>> endPoints = _endpoints.entrySet(); 
		for (Entry<Seal, SocketAddress> entry : endPoints) {
			SocketAddress entrySocketAddress = entry.getValue();
			if (entrySocketAddress.equals(packetEndPoint)) {
				sender = entry.getKey();
				break;
			}
		}
		return sender;
	}

	
	private Signal<Integer> ownPort() {
		return my(Attributes.class).myAttributeValue(OwnPort.class);
	}
	

	final class UdpPacket implements Packet {
		private final byte[] _data;
		private final Seal _sender;
		
		UdpPacket(byte[] data,  Seal sender) {
			_data = data;
			_sender = sender;
		}
		
		
		@Override
		public Seal sender() {
			return _sender;
		}

		
		@Override
		public byte[] data() {
			return _data;
		}
	}
}
