package sneer.bricks.hardware.cpu.algorithms.crypto.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import sneer.bricks.hardware.cpu.algorithms.crypto.Digester;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;

class FileDigest {

	private static final int FILE_BLOCK_SIZE = 1024 * 100;

	private final FileInputStream _input;
	private final Digester _digester;
	private final byte[] _buffer = new byte[FILE_BLOCK_SIZE];
	private volatile Contract _contract;

	private final Latch _ready = my(Latches.class).produce();
	private IOException _exception;
	private Sneer1024 _result;


	FileDigest(File file, Digester digester) throws IOException {
		_input = new FileInputStream(file);
		_digester = digester;

		_contract = my(Threads.class).startStepping(new Runnable() { @Override public void run() {
			step();
		}});
	}

	
	private void step() {
		try {
			tryToStep();
		} catch (IOException e) {
			finishWith(e);
		}
	}


	private void tryToStep() throws IOException {
		int numOfBytes = _input.read(_buffer);
		if (numOfBytes == -1)
			finishWith(_digester.digest());
		else
			_digester.update(_buffer, 0, numOfBytes);
	}

	
	private void finishWith(Object result) {
		try { _input.close(); } catch (Throwable ignore) {}
		
		while (_contract == null) Thread.yield();
		_contract.dispose();
		
		if (result instanceof IOException)
			_exception = (IOException)result;
		else
			_result = (Sneer1024)result;

		_ready.open();
	}

	
	Sneer1024 result() throws IOException {
		_ready.waitTillOpen();
		if (_exception != null) throw _exception;
		return _result;
	}

}
