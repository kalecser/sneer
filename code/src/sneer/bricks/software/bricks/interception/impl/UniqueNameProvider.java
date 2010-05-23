package sneer.bricks.software.bricks.interception.impl;



public class UniqueNameProvider {

	private static int _id;

	public static String uniqueName(String token) {
		return token + "$" +  (++_id); 
	}

}
