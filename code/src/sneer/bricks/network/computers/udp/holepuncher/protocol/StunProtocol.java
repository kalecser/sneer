package sneer.bricks.network.computers.udp.holepuncher.protocol;

import java.net.InetSocketAddress;

import basis.brickness.Brick;


@Brick
public interface StunProtocol {

	int marshalRequestTo(StunRequest request, byte[] requestBytes);
	StunRequest unmarshalRequest(byte[] data, int length);

	int marshalReplyTo(StunReply reply, byte[] data);
	StunReply unmarshalReply(byte[] data, int length);
	
	InetSocketAddress serverAddress();

}
