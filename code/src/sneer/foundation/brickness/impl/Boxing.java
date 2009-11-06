package sneer.foundation.brickness.impl;


public class Boxing {
	
	public static int unboxInt(Object value) {
		return ((Integer)value).intValue();
	}

	public static Integer box(int value) {
		return new Integer(value);
	}

}
