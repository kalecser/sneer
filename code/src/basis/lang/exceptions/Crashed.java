package basis.lang.exceptions;

import java.io.IOException;

public class Crashed extends IOException {
	
	public Crashed() {
		super("This component has already been crashed.");
	}
	
}
