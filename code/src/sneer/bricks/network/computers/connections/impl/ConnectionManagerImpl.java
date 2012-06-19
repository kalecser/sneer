package sneer.bricks.network.computers.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.computers.tcp.connections.TcpConnectionManager;
import sneer.bricks.network.computers.tcp.connections.originator.SocketOriginator;
import sneer.bricks.network.computers.tcp.connections.receiver.SocketReceiver;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.computers.udp.sender.UdpSender;
import sneer.bricks.network.computers.udp.server.UdpServer;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;
import basis.lang.Consumer;


class ConnectionManagerImpl implements ConnectionManager {

	private static final boolean USE_UDP = false;
	private final Delegate delegate = USE_UDP
		? my(UdpConnectionManager.class)
		: my(TcpConnectionManager.class);
	
	{
		if (USE_UDP) {
			my(UdpServer.class);
			my(StunClient.class).initSender(new Consumer<DatagramPacket>() { @Override public void consume(DatagramPacket packet) {
				my(UdpSender.class).send(packet);
			}});
		} else {
			my(SocketOriginator.class);
			my(SocketReceiver.class);
		}
	}
	
	
	@Override
	public ByteConnection connectionFor(Contact contact) {
		return delegate.connectionFor(contact);
	}

	
	@Override
	public void closeConnectionFor(Contact contact) {
		delegate.closeConnectionFor(contact);
	}

	
	@Override
	public Source<Call> unknownCallers() {
		return delegate.unknownCallers();
	}

}
