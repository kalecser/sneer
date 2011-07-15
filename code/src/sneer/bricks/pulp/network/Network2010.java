package sneer.bricks.pulp.network;

import java.io.IOException;

import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Network2010 {
	
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


interface CompositeNetwork extends Network2010 {
	
	void add(Network2010 network);
	
}

interface UdpNetwork extends Network2010 {}
interface TcpNetwork extends Network2010 {}
interface HttpTunnelNetwork extends Network2010 {}
