package sneer.bricks.pulp.network;

import java.io.IOException;

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
//	public interface Protocol {
//		boolean canSendTo(Object destinationId);
//		void send(byte[] data, Object destinationId);
//	}
//	
//	public interface Packet {
//		Object senderId();
//		byte[] data();
//	}
//
//	//Aplicacao usando:
//	void send(byte[] data, Object destinationId);
//	EventSource<Packet> packetsReceived();
//	
//	//Protocolo usando:
//	void registerToSend(Protocol protocol);
//	void receive(byte[] data, Object senderId);

}
