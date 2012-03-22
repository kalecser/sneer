package spikes.adenauer.network.udp.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import spikes.adenauer.network.UdpNetworkSpike;
import spikes.adenauer.network.udp.UdpAddressResolver;

public class UdpNetworkImpl implements UdpNetworkSpike {
	
	private DatagramSocket socket;

	private final DatagramPacket _packetToReceive = newPacket();
	private final DatagramPacket _packetToSend = newPacket();

	private final Notifier<Packet> _notifier = my(Notifiers.class).newInstance();
	private final SetRegister<Seal> _peersOnline = my(CollectionSignals.class).newSetRegister();

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
			Seal sender = packet.sender();
			_peersOnline.add(sender);
			_notifier.notifyReceivers(packet);
		//}	
	}

	@Override
	public SetSignal<Seal> peersOnline() {
		return _peersOnline.output();
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
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	
	@Override
	public Source<Packet> packetsReceived() {
		return _notifier.output();
	}

	
	private DatagramPacket newPacket() {
		byte[] array = new byte[UdpNetworkSpike.MAX_ARRAY_SIZE];
		return new DatagramPacket(array, array.length);
	}
	
	
	private Packet newPacket(DatagramPacket pac) {
		byte[] data = new byte[pac.getLength()];
		System.arraycopy(pac.getData(), pac.getOffset(), data, 0, data.length);
		SocketAddress addressFrom = pac.getSocketAddress();
		Seal sender = my(UdpAddressResolver.class).sealFor(addressFrom);
		return new UdpPacket(data, sender);
	}

	
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
