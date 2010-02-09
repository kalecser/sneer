package sneer.bricks.hardware.io.log.filter;

import sneer.bricks.pulp.tuples.Tuple;

public class LogWhiteListEntry extends Tuple {

	public final String phrase;

	public LogWhiteListEntry(String phrase_) {
		phrase = phrase_;
	}	

}
