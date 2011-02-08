package sneer.bricks.softwaresharing;

import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.pulp.reactive.Signal;

public interface BrickVersion {

	public enum Status {
		/** The brick version you are currently using. It has only CURRENT files. (Icon: Black folder)*/
		CURRENT, 
		
		/** A version that has evolved independently of your current version. (The author of this version has NOT explicitly rejected your current version) (Icon: Red folder with pencil)*/
		DIVERGING,
		
		/** A version that is different from your current version. (Icon: Black folder with pencil)*/
		DIFFERENT,

		/** A version that you don't want. When you stage a version for execution, the current version becomes REJECTED automatically. @See {@link BrickVersion#setRejected(boolean)} (Icon: Light gray folder)*/
		REJECTED
	}
	
	Hash hash();
	
	Signal<Status> status();
	boolean isChosenForExecution();
	void setRejected(boolean rejected);
	
	long publicationDate();
	
	List<String> users();
	
	List<FileVersion> files();

	FileVersion file(String relativePath);
}
