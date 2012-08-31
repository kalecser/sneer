package sneer.bricks.network.computers.connections;

import java.nio.ByteBuffer;

import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Consumer;
import basis.lang.Producer;

public interface ByteConnection {

	Signal<Boolean> isConnected();

	void initCommunications(Producer<? extends ByteBuffer> sender, Consumer<? super ByteBuffer> receiver);

	
}