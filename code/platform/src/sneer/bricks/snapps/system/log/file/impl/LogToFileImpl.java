package sneer.bricks.snapps.system.log.file.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.workers.notifier.LogNotifier;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.snapps.system.log.file.LogToFile;
import sneer.foundation.lang.Consumer;

class LogToFileImpl implements LogToFile {

	private FileWriter _logWriter;
	@SuppressWarnings("unused")	private WeakContract _loggedMessagesContract;

	
	@Override
	public void startWritingLogTo(File logFile) {
		if (_logWriter != null) throw new IllegalStateException("Logging to file was already started.");
		_logWriter = initFileWriter(logFile);
		
		_loggedMessagesContract = my(LogNotifier.class).loggedMessages().addReceiver(new Consumer<String>(){ @Override public void consume(String msg) {
			write(msg);
		}});
	}
	
	
	private FileWriter initFileWriter(File logFile) {
		if(!logFile.getParentFile().exists())
			logFile.getParentFile().mkdirs();

		try {
			boolean append = true;
			return new FileWriter(logFile, append);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error opening log file.", null, e);
			return null;
		}
	}


	private void write(String msg){
		try {
			_logWriter.write(msg);
			_logWriter.flush();
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error writing to log file.", null, e);
		}
	}

	
}