package sneer.bricks.hardware.io.log.filter.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import sneer.bricks.hardware.io.log.filter.LogFilter;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;

class LogFilterImpl implements LogFilter {

	private final ListRegister<String> _phrases = my(CollectionSignals.class).newListRegister();
	{
		_phrases.add("Exception");
		_phrases.add("thrown");
//		_phrases.add("online");
//		_phrases.add("offline");
//		_phrases.add("Tuple");
		_phrases.add("Fetch");
		_phrases.add("Track");
		_phrases.add("File");
//		_phrases.add("Shout");
//		_phrases.add("Stepper");
//		_phrases.add("Sending");
//		_phrases.add("[");
	}
	
	@Override
	public ListRegister<String> whiteListEntries() {
		return _phrases;
	}

	@Override
	public boolean acceptLogEntry(String message) {
		List<String> whiteList = _phrases.output().currentElements();
		for (String entry : whiteList) 
			if(message.contains(entry)) return true;
		
		return false;
	}	
}
