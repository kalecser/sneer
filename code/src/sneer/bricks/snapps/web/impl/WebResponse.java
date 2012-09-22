package sneer.bricks.snapps.web.impl;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class WebResponse extends Tuple {
	
	public WebResponse(Seal publisher, String contents) {
		super(publisher);
		_contents = contents; 
	}

	public String _contents;
}
