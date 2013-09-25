package sneer.bricks.network.computers.udp.holepuncher.protocol;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import basis.brickness.Brick;


@Brick
public interface StunProtocol {

	void marshalRequestTo(StunRequest request, ByteBuffer out);
	StunRequest unmarshalRequest(ByteBuffer in);

	void marshalReplyTo(StunReply reply, ByteBuffer out);
	StunReply unmarshalReply(ByteBuffer in);
	
	InetAddress serverHost();
	int serverPort();

}
