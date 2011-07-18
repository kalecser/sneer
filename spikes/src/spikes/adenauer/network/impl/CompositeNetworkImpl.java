package spikes.adenauer.network.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import spikes.adenauer.network.CompositeNetwork;
import spikes.adenauer.network.Network;
import spikes.adenauer.network.UdpNetwork;

public class CompositeNetworkImpl implements CompositeNetwork {
	protected SetSignal<Seal> _peersOnline = my(SetSignal.class);
	protected Network _defaultNetwork; 
	
	public CompositeNetworkImpl() {
		add(my(UdpNetwork.class));
	}
	
	
	@Override
	public SetSignal<Seal> peersOnline() {
		return _peersOnline;
	}

	
	@Override
	public void send(byte[] data, Seal destination) throws IOException {
		_defaultNetwork.send(data, destination);
	}

	
	@Override
	public EventSource<Packet> packetsReceived() {
		return _defaultNetwork.packetsReceived();
	}

	
	@Override
	public void add(Network network) {
		if (null == network) return;
		_defaultNetwork = network;
	}
	
}
