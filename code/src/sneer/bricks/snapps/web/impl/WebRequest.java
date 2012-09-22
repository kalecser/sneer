package sneer.bricks.snapps.web.impl;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class WebRequest extends Tuple {
	
	public String _url;
	
	
	public WebRequest(Seal addressee) {
		super(addressee);
	}

}
