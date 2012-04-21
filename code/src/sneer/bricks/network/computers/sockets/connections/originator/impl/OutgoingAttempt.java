package sneer.bricks.network.computers.sockets.connections.originator.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.sockets.connections.SocketConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network2010;
import basis.lang.Closure;
import basis.lang.Consumer;

class OutgoingAttempt {

	private final Network2010 _network = my(Network2010.class);
	private final SocketConnectionManager _socketConnectionManager = my(SocketConnectionManager.class);
	private final InternetAddress _address;
	private int _port;
	private final WeakContract _steppingContract;
	private final Light _light = my(BlinkingLights.class).prepare(LightType.WARNING);
	
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;

	
	OutgoingAttempt(final InternetAddress address) {
		_address = address;
		_refToAvoidGc = _address.port().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
			_port = port;
		}});

		_steppingContract = my(Timer.class).wakeUpNowAndEvery(20 * 1000, new Closure() { @Override public String toString() { return "Outgoing Connection Attempt: " + address; } @Override public void run() {
			tryToOpen();
		}});
	}

	
	private void tryToOpen() {
		if (hasSocketAlready()) return;
		if (!contactHasSeal()) return;
		if (_port <= 0) return;
		
		my(Logger.class).log("Trying to open socket to: {} host: {} port: {}", contact(), _address.host(), _port);

		ByteArraySocket socket;
		try {
			socket = _network.openSocket(_address.host(), _port);
		} catch (IOException e) {
			my(Logger.class).log("{} host: {} port: ", e.getMessage(),_address.host(), _port);
			return;
		}

		my(Logger.class).log("Socket opened to: {} port: {}", _address.host(), _port);
		_socketConnectionManager.manageOutgoingSocket(socket, contact());
	}


	private boolean contactHasSeal() {
		if (my(ContactSeals.class).sealGiven(contact()).currentValue() == null) {
			my(BlinkingLights.class).turnOnIfNecessary(_light, "" + contact() + "'s Seal is unknown.", "You will be able to connect to this contact once you have entered his Seal. Right-click on the contact and choose 'Edit Contact' (or something like that :)");
			return false;
		}
		my(BlinkingLights.class).turnOffIfNecessary(_light);
		return true;
	}


	private Contact contact() {
		return _address.contact();
	}

	
	private boolean hasSocketAlready() {
		return _socketConnectionManager.socketConnectionFor(contact()).isConnected().currentValue();
	}

	
	void crash() {
		_steppingContract.dispose();
	}
	
}
