package sneer.foundation.brickness.testsupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.Consumer;

public class LoggerMocks {

	private final List<String> _allInstanceNames = new ArrayList<String>();
	private final List<String> _keptMessages = Collections.synchronizedList(new ArrayList<String>());

	
	public Logger newInstance() {
		return newInstance("Log"); 
	}

	
	synchronized
	public Logger newInstance(String name) {
		_allInstanceNames.add(name);
		return new LoggerMock(messageKeeper(prefix(name))); 
	}

	
	private Consumer<String> messageKeeper(final String prefix) {
		return new Consumer<String>() { @Override public void consume(String message) {
			_keptMessages.add(prefix + message);
		}};
	}

	
	private String prefix(String name) {
		return name + count(name) + ": ";
	}

	
	private String count(String prefix) {
		int result = 0;
		for (String existing : _allInstanceNames)
			if (existing.equals(prefix)) result++;
		return result == 1 ? "" : " " + result;
	}

	
	void printAllKeptMessages() {
		for (String message : _keptMessages.toArray(new String[0]))
			System.out.println(message);
	}

}
