package sneer.bricks.hardware.io.log.filter;

import sneer.bricks.expression.tuples.Tuple;

public class LogWhiteListEntry extends Tuple {

	public final String phrase;

	public LogWhiteListEntry(String phrase_) {
		phrase = phrase_;
	}	

}
