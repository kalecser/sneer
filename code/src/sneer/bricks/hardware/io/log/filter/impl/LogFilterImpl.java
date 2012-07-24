package sneer.bricks.hardware.io.log.filter.impl;

import static basis.environments.Environments.my;

import java.util.List;

import sneer.bricks.hardware.io.log.filter.LogFilter;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;

class LogFilterImpl implements LogFilter {

	private final ListRegister<String> _phrases = my(CollectionSignals.class).newListRegister();
	{
		_phrases.add("thrown");
		_phrases.add("Blinking Light");
		_phrases.add("online");
		_phrases.add("offline");
		_phrases.add("Transaction");
		_phrases.add("Fetch");
		_phrases.add("Mapping");
		_phrases.add("key");
		_phrases.add("peak");
		_phrases.add("Thread count");
		_phrases.add("block: 0");
		_phrases.add("Endorsement");
		_phrases.add("Brick");
		_phrases.add("Enter");
		_phrases.add("Attribute");
		_phrases.add("Stun");
		_phrases.add("Packet");
		_phrases.add("Sleep");
	}
	
	@Override
	public ListRegister<String> whiteListEntries() {
		return _phrases;
	}

	@Override
	public boolean isLogEntryAccepted(String message) {
		List<String> whiteList = _phrases.output().currentElements();
		for (String entry : whiteList) 
			if(message.contains(entry)) return true;
		
		return false;
	}	
}
