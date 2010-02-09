package sneer.bricks.softwaresharing;

import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;

public interface BrickVersion {

	public enum Status {
		/** The brick version you are currently using. It has only CURRENT files. */
		CURRENT, 
		
		/** A version that is different from your current version. */
		DIFFERENT,

		/** A version that has evolved independently of your current version. (The author of this version has NOT explicitly rejected your current version) */
		DIVERGING,
		
		/** A version that you don't want. When you stage a version for execution, the current version becomes REJECTED automatically. @See {@link BrickVersion#setRejected(boolean)} */
		REJECTED
	}
	
	Sneer1024 hash();
	
	Status status();
	boolean isStagedForExecution();
	void setRejected(boolean rejected);
	
	long publicationDate();
	
	List<String> users();
	
	List<FileVersion> files();
}
