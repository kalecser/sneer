package sneer.bricks.snapps.web.impl;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.seals.Seal;
import static basis.environments.Environments.my;


public class WebRequest extends Tuple {
	
	public String _url;
	
	
	public WebRequest(Seal addressee) {
		super(addressee);
	}


	public void respond(String contents) {
		WebResponse response = new WebResponse(publisher, contents);
		my(TupleSpace.class).add(response);
	}

}
