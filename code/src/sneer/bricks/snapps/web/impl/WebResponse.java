package sneer.bricks.snapps.web.impl;

import sneer.bricks.expression.tuples.Tuple;

public class WebResponse extends Tuple {
	
	public WebResponse(String contents) {
		_contents = contents;
	}

	public String _contents;
}
