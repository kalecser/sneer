package sneer.bricks.pulp.network;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.collections.SetSignal;

public interface Network {
	
//	//////////////////////////////////////
//	
//	// Mesmas garantias que o UDP (Nao garante entrega, nem ordem)
//	//   + criptografia
//	//   + autenticacao
//
//	
	
	SetSignal<Seal> peersOnline();
	void send(byte[] data, Seal destination);
	EventSource<Packet> packetsReceived();
	
	public interface Packet {
		Seal sender();
		byte[] data();
	}
	
}

//Brick
interface CompositeNetwork extends Network {
	
	void add(Network network);
	
}

//Bricks
interface UdpNetwork {}
interface TcpNetwork extends Network {}
interface HttpTunnelNetwork extends Network {}
