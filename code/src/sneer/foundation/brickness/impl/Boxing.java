package sneer.foundation.brickness.impl;

public class Boxing {
	
	public static int unboxInt(Object value) { return ((Integer)value).intValue(); }

	public static boolean unboxBoolean(Object value) { return ((Boolean)value).booleanValue(); }
	
	public static Integer box(int value) { return new Integer(value); }
	
	public static Boolean box(boolean value) { return new Boolean(value); }

}
