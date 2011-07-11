package sneer.bricks.pulp.network;

import java.io.IOException;

import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Network {
	
	ByteArraySocket openSocket(String remoteAddress, int remotePort) throws IOException;
	
	ByteArrayServerSocket openServerSocket(int port) throws IOException;
	
	String remoteIpFor(ByteArraySocket socket);

	
//	//////////////////////////////////////
//	
//	// Mesmas garantias que o UDP (Nao garante entrega, nem ordem)
//	//   + criptografia
//	//   + autenticacao
//
//	
	
//	SetSignal<Seal> peersOnline();
//	void send(byte[] data, Seal destination);
//	EventSource<Packet> packetsReceived();
//	
	public interface Packet {
		Seal sender();
		byte[] data();
	}
	
}


interface CompositeNetwork extends Network {
	
	void add(Network network);
	
}

interface UdpNetwork extends Network {}
interface TcpNetwork extends Network {}
interface HttpTunnelNetwork extends Network {}
