package spikes.adenauer.network.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.arrays.ImmutableByteArray;
import spikes.adenauer.network.CompositeNetwork;
import spikes.adenauer.network.Network.Packet;


public class NetworkTest extends BrickTestBase {
	private final Threads _threads = my(Threads.class);

	@Test	
	public void udpNetworkTest() throws Exception {
		_threads.startDaemon("Loopback peer", new LoopbackPeer());
	}
	
	final class LoopbackPeer implements Closure {
		private final CompositeNetwork _network = Environments.my(CompositeNetwork.class);
		
		@SuppressWarnings("unused")	private WeakContract _regToAvoidGC;
		
		public LoopbackPeer() {
			_regToAvoidGC = networkReceiver().addReceiver(new Consumer<Packet>(){ @Override public void consume(Packet packet) {
				loopback(packet);
			}});
		} 

		@Override
		public void run() {
		}

		private void loopback(Packet packet) {
			String data = new String(packet.data()).toUpperCase();
			try {
				_network.send(data.getBytes(), dummySeal());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		private EventSource<Packet> networkReceiver() {
			return _network.packetsReceived();
		}
		
		
		private Seal dummySeal() {
			try {
				return new Seal(new ImmutableByteArray(my(Codec.class).hex().decode("c0e0ae71b239640fded22b880f7cb63772f04a6f9c7685689c2610f395dbff1d3cfc11f36f20d54305ff51b26cd171e4882d628ea4a1ac201641cf17fea6912c")));
			} catch (DecodeException e) {
				throw new IllegalStateException(e);
			}
		}
	}	
}
