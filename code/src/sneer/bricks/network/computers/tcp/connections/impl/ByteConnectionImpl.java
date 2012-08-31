package sneer.bricks.network.computers.tcp.connections.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.pulp.bandwidth.BandwidthCounter;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Producer;

class ByteConnectionImpl implements ByteConnection {

	static private final Threads Threads = my(Threads.class);
	private final BandwidthCounter _bandwidthCounter = my(BandwidthCounter.class);
	private int _uploadPause;

	private final SocketHolder _socketHolder = new SocketHolder();
	
	private Producer<? extends ByteBuffer> _sender;
	private Consumer<? super ByteBuffer> _receiver;
	
	private Contract _contractToSend;
	private Contract _contractToReceive;


	@Override
	public Signal<Boolean> isConnected() {
		return _socketHolder.isConnected();
	}


	@Override
	public void initCommunications(Producer<? extends ByteBuffer> sender, Consumer<? super ByteBuffer> receiver) {
		if (_sender != null) throw new IllegalStateException();
		_sender = sender;
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

		ByteBuffer byteBuffer = _sender.produce();
		byte[] bytes = new byte[byteBuffer.remaining()]; 
		byteBuffer.get(bytes);
		try {
			socket.write(bytes);
		} catch (IOException iox) {
			_socketHolder.close(socket, "Error trying to send packet.", iox);
			return;
		}

		_bandwidthCounter.sent(bytes.length);
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
		_receiver.consume(ByteBuffer.wrap(array));
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