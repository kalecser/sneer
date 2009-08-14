package sneer.bricks.softwaresharing;

public interface FileVersion {

	public enum Status {
		/**File that exists in this version but does not exist in the current version.*/
		EXTRA,
		
		/**File that has the same contents as the current version.*/
		CURRENT,
		
		/**File that has different contents in the current version.*/
		DIFFERENT,
		
		/**File that exists in the current version but does not exist in this version.*/
		MISSING
	}

	String name();
	Status status();

	byte[] contents();
	/** Used for diff comparisons. Returns null if the brick that contains this file is not currently being used (new or deleted brick). Returns the same as contents() if the status is CURRENT. */
	byte[] contentsInCurrentVersion();
	
}
