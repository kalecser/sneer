package sneer.bricks.network.computers.sockets.connections.receiver.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import basis.lang.Consumer;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.sockets.accepter.SocketAccepter;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.receiver.SocketReceiver;
import sneer.bricks.pulp.network.ByteArraySocket;

class SocketReceiverImpl implements SocketReceiver {

	private final SocketAccepter _socketAccepter = my(SocketAccepter.class);
	
	private final Threads _threads = my(Threads.class);

	@SuppressWarnings("unused") private final Object _receptionRefToAvoidGc;

	SocketReceiverImpl() {
		_receptionRefToAvoidGc = _socketAccepter.lastAcceptedSocket().addReceiver(new Consumer<ByteArraySocket>() { @Override public void consume(final ByteArraySocket socket) {
			_threads.startDaemon("SocketReceiverImpl", new Closure() { @Override public void run() {
				my(ConnectionManager.class).manageIncomingSocket(socket);
			}});
		}});
	}
}