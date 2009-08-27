package sneer.foundation.brickness.testsupport;

import sneer.bricks.hardware.io.log.LogWorker;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.Consumer;

class LoggerMock implements Logger {

	private final Consumer<String> _messageConsumer;

	
	LoggerMock(Consumer<String> messageConsumer) {
		_messageConsumer = messageConsumer;
	}


	@Override
	public void log(String message, Object... messageInsets) {
		String formatted = format(message, messageInsets);
		if (formatted.contains("Heartbeat")) return; ///////////////////// Message to filter out.
		if (formatted.contains("Tuple")) return; ///////////////////// Message to filter out.
		
		_messageConsumer.consume(formatted);
	}

	
	@Override
	public void setDelegate(LogWorker worker) {
		throw new UnsupportedOperationException();
	}

	
	String format(String message, Object... messageInsets) {
		StringBuilder result = new StringBuilder();
		formatInsets(result, message, messageInsets);
		return result.toString();
	}


	static private void formatInsets(StringBuilder builder, String message, Object... messageInsets) {
		String[] parts = message.split("\\{\\}");
		int i = 0;
		while (true) {
			if (i == parts.length) break;
			builder.append(parts[i]);
		
			if (i == messageInsets.length) break;
			builder.append(messageInsets[i]);
			i++;
		}
	}
	
}
