package sneer.bricks.hardware.io.log.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.Consumer;

public class LoggerMocks {

	static public boolean showLog = false;

	private final List<String> _allInstanceLabels = new ArrayList<String>();
	private final List<String> _keptMessages = Collections.synchronizedList(new ArrayList<String>());

	
	public Logger newInstance() {
		return newInstance("Log"); 
	}

	
	synchronized
	public Logger newInstance(String name) {
		String label = (name + "   ").substring(0,3);
		_allInstanceLabels.add(label);
		return new LoggerMock(messageKeeper(prefix(label))); 
	}

	
	private Consumer<String> messageKeeper(final String prefix) {
		return new Consumer<String>() { @Override public void consume(String message) {
//			if (!message.contains("Y.java")) return;
			_keptMessages.add(prefix + message);
			if (!showLog) return;
			System.out.println(prefix + message);
		}};
	}

	
	private String prefix(String label) {
		return label + count(label) + ": ";
	}

	
	private String count(String prefix) {
		int result = 0;
		for (String existing : _allInstanceLabels)
			if (existing.equals(prefix)) result++;
		return result == 1
			? "  "
			: pad(result);
	}

	
	private String pad(int number) {
		String result = " " + number;
		return result.substring(result.length() - 2);
	}


	public void printAllKeptMessages() {
		for (String message : _keptMessages.toArray(new String[0]))
			System.out.println(message);
	}

}
