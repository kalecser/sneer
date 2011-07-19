package spikes.adenauer.network.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import spikes.adenauer.network.Network.Packet;


public class UdpSupportTest extends BrickTestBase {
	private final Threads _threads = my(Threads.class);
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;
	
	public UdpSupportTest() {
		_refToAvoidGC = peerPacketReceiver().addReceiver(new Consumer<Packet>() { @Override public void consume(Packet packet) {
			String data = new String(packet.data());
			assertEquals(data, "HELLO");
		}});
	}
	
	
	@Test	
	public void udpNetworkTest() throws Exception {	
		_threads.startDaemon("Loopback peer", new LoopbackPeer());
//		_peerNetwork.add(my(UdpNetwork.class));
//		_peerNetwork.send("hello".getBytes(), dummySeal()); 
	}
	
	
	private EventSource<Packet> peerPacketReceiver() {
//		return _peerNetwork.packetsReceived();
		return null;
	}
	
	
//	private Seal dummySeal() {
//		try {
//			return new Seal(new ImmutableByteArray(my(Codec.class).hex().decode("c0e0ae71b239640fded22b880f7cb63772f04a6f9c7685689c2610f395dbff1d3cfc11f36f20d54305ff51b26cd171e4882d628ea4a1ac201641cf17fea6912c")));
//		} catch (DecodeException e) {
//			throw new IllegalStateException(e);
//		}
//	}

	
	final class LoopbackPeer implements Closure {
		
		@SuppressWarnings("unused")	private WeakContract _refToAvoidGC2;
		
		public LoopbackPeer() {
//			_refToAvoidGC2 = networkReceiver().addReceiver(new Consumer<Packet>(){ @Override public void consume(Packet packet) {
//				loopback(packet);
//			}});
		} 

		@Override
		public void run() {
			//_network.add(my(UdpNetwork.class));
		}

//		private void loopback(Packet packet) {
//			String data = new String(packet.data()).toUpperCase();
//			try {
//				_network.send(data.getBytes(), dummySeal());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		
//		private EventSource<Packet> networkReceiver() {
//			return _network.packetsReceived();
//		}
	}
	
}
