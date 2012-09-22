package snype.whisper.skin.audio.loopback.impl;

import static basis.environments.Environments.my;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import snype.whisper.skin.audio.kernel.Audio;
import basis.lang.Closure;
class Recorder {
	
	static private ByteArrayOutputStream _buffer;
	private static Contract _stepperContract;
	private static TargetDataLine _targetDataLine;

	static void stop() {
		_stepperContract.dispose();
		if (_targetDataLine != null)
			_targetDataLine.close();
	}
	
	static boolean start(ByteArrayOutputStream buffer) {
		_targetDataLine = tryToOpenCaptureLine();
		if (_targetDataLine == null) return false;
		
		_buffer = buffer;

		_stepperContract = my(Threads.class).startStepping(new Closure() { @Override public void run() {
			record(_targetDataLine);
		}});
		return true;
	}

	private static TargetDataLine tryToOpenCaptureLine() {
		try {
			return my(Audio.class).tryToOpenCaptureLine();
		} catch (LineUnavailableException e) {
			return null;
		}
	}

	private static void record(TargetDataLine targetDataLine) {
		byte tmpArray[] = new byte[1024];
		int cnt = targetDataLine.read(tmpArray, 0, tmpArray.length);
		if (cnt == 0) return;

		synchronized (_buffer) {
			_buffer.write(tmpArray, 0, cnt);
		}
	}

}