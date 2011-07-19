package spikes.adenauer.network.udp.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Closure;
import spikes.adenauer.network.Network;
import spikes.adenauer.network.udp.UdpAddressResolver;

public class UdpNetworkImpl implements Network {
	
	private DatagramSocket socket;

	private final DatagramPacket _packetToReceive = newPacket();
	private final DatagramPacket _packetToSend = newPacket();

	private final EventNotifier<Packet> _notifier = my(EventNotifiers.class).newInstance();

	private final Light errorOnReceive = my(BlinkingLights.class).prepare(LightType.ERROR);

	
	public UdpNetworkImpl(int port) throws SocketException {
		socket = new DatagramSocket(port);
		my(Threads.class).startStepping("UDP Packet Receiver", new Closure() { @Override public void run() {
			tryToReceivePacket();
		}});
	}

	
	private void tryToReceivePacket() {
		try {
			receivePacket();
			my(BlinkingLights.class).turnOffIfNecessary(errorOnReceive);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(errorOnReceive, "Error trying to receive UDP packet.", e);
		}
	}

	
	private void receivePacket() throws IOException {
		socket.receive(_packetToReceive);
		
		//if (endpointsBySeal.containsValue( _packetToReceive.getSocketAddress())) {
			Packet packet = newPacket(_packetToReceive); 
			_notifier.notifyReceivers(packet);
		//}	
	}

	@Override
	public SetSignal<Seal> peersOnline() {
		return null;
	}

	@Override
	public synchronized void send(byte[] data, Seal destination) {
		SocketAddress address = my(UdpAddressResolver.class).addressFor(destination);
		if (address == null) return;
		
		_packetToSend.setData(data);
		_packetToSend.setSocketAddress(address);
		try {
			socket.send(_packetToSend);
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	
	@Override
	public EventSource<Packet> packetsReceived() {
		return _notifier.output();
	}

	
	private DatagramPacket newPacket() {
		byte[] array = new byte[Network.MAX_ARRAY_SIZE];
		return new DatagramPacket(array, array.length);
	}
	
	
	private Packet newPacket(DatagramPacket receivedPacket) {
		byte[] data = receivedPacket.getData();
//		Seal sender = findSenderFromEndPoints(receivedPacket.getSocketAddress());
		Seal sender = null;
		return new UdpPacket(data, sender);
	}

	
//	private Seal findSenderFromEndPoints(SocketAddress packetEndPoint) {
//		for (Entry<Seal, SocketAddress> entry : endpointsBySeal.entrySet()) {
//			SocketAddress entrySocketAddress = entry.getValue();
//			if (entrySocketAddress.equals(packetEndPoint))
//				return entry.getKey();
//		}
//		return null;
//	}

	
//	private Signal<Integer> ownPort() {
//		return my(Attributes.class).myAttributeValue(OwnPort.class);
//	}
	

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


	public void close() {
		socket.close();
	}
}
