package sneer.bricks.snapps.wackup;

import sneer.bricks.expression.tuples.Tuple;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class NewFile extends Tuple {

	public final String _name;
	public final ImmutableByteArray _content;

	public NewFile(String name, ImmutableByteArray content) {
		_name = name;
		_content = content;
	}

}
