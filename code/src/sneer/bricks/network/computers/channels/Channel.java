package sneer.bricks.network.computers.channels;

import java.nio.ByteBuffer;

import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;
import basis.lang.Producer;


public interface Channel {
	
	long id();
	
	void open(Producer<? extends ByteBuffer> sender, Consumer<? super ByteBuffer> receiver);
	
	Signal<Boolean> isUp();
	
	int maxPacketSize();

}
