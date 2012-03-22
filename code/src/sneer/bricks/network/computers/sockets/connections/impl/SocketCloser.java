package sneer.bricks.network.computers.sockets.connections.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import basis.lang.ClosureX;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.network.ByteArraySocket;

class SocketCloser {

	static void closeIfUnsuccessful(ByteArraySocket socket,	String message, ClosureX<IOException> closure) {
		try {
			closure.run();
		} catch (IOException e) {
			close(socket, message, e);
		}
	}


	static void close(ByteArraySocket socket, String message, IOException e) {
		close(socket, message + " " + e.getMessage());
	}

	
	static void close(ByteArraySocket socket, String message) {
		socket.close();
		my(Logger.class).log(message);
	}

}
