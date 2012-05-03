package sneer.bricks.network.computers.tcp.connections.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.pulp.bandwidth.BandwidthCounter;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Closure;
import basis.lang.Consumer;

class ByteConnectionImpl implements ByteConnection {

	static private final Threads Threads = my(Threads.class);
	private final BandwidthCounter _bandwidthCounter = my(BandwidthCounter.class);
	private int _uploadPause;

	private final SocketHolder _socketHolder = new SocketHolder();
	
	private PacketScheduler _scheduler;
	private Consumer<? super byte[]> _receiver;
	
	private Contract _contractToSend;
	private Contract _contractToReceive;


	@Override
	public Signal<Boolean> isConnected() {
		return _socketHolder.isConnected();
	}


	@Override
	public void initCommunications(PacketScheduler sender, Consumer<? super byte[]> receiver) {
		if (_scheduler != null) throw new IllegalStateException();
		_scheduler = sender;
		_receiver = receiver;

		startSending();
		startReceiving();
	}

	
	private void startSending() {
		_contractToSend = Threads.startStepping(new Closure() { @Override public void run() {
			send(_socketHolder.waitForSocket());
		}});
	}

	
	private void startReceiving() {
		_contractToReceive = Threads.startStepping(new Closure() { @Override public void run() {
			receiveFrom(_socketHolder.waitForSocket());
		}});
	}
	
	
	private void send(ByteArraySocket socket) {
		throttleUpload();
		if (tryToSend(_scheduler.highestPriorityPacketToSend(), socket))
			_scheduler.previousPacketWasSent();
	}


	private boolean tryToSend(byte[] array, ByteArraySocket mySocket) {
		try {
			mySocket.write(array);
		} catch (IOException iox) {
			_socketHolder.close(mySocket, "Error trying to send packet.", iox);
			return false;
		}

		_bandwidthCounter.sent(array.length);
		return true;
	}


	private void receiveFrom(ByteArraySocket mySocket) {
		byte[] array;
		try {
			array = mySocket.read();
		} catch (IOException iox) {
			_socketHolder.close(mySocket, "Error trying to receive packet.", iox);
			return;
		}

		_bandwidthCounter.received(array.length);
		_receiver.consume(array);
	}

	
	private void throttleUpload() {
		int delta = uploadSpeed() > 50 ? 10 : -10;
		_uploadPause = _uploadPause + delta;
		if (_uploadPause < 0) _uploadPause = 0;
		if (_uploadPause == 0) return;
		
		my(Threads.class).sleepWithoutInterruptions(_uploadPause);
	}


	private Integer uploadSpeed() {
		return _bandwidthCounter.uploadSpeedInKBperSecond().currentValue();
	}


	void close() {
		if (_contractToSend != null) _contractToSend.dispose();
		if (_contractToReceive != null) _contractToReceive.dispose();
		
		_socketHolder.close("Connection closed.");
	}


	SocketHolder socketHolder() {
		return _socketHolder;
	}

}