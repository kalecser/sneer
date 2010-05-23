package sneer.bricks.software.bricks.interception;

public class Boxing {
	
	public static int unboxInt(Object value) { return ((Integer)value).intValue(); }

	public static boolean unboxBoolean(Object value) { return ((Boolean)value).booleanValue(); }
	
	public static Object box(int value) { return new Integer(value); }
	
	public static Object box(boolean value) { return new Boolean(value); }

}
