package sneer.bricks.softwaresharing;

import java.util.List;

import sneer.bricks.pulp.reactive.Signal;

public interface BrickHistory {
	
	public enum Status {
		/** A brick that has only rejected versions. (Icon: Light gray)*/
		REJECTED,

		/** A brick that has one or more DIVERGING versions. (Icon: Red with pencil)*/
		DIVERGING,
		
		/** A brick that has no CURRENT version and has one or more DIFFERENT versions. (Icon: Yellow)*/
		NEW,
		
		/** A brick that has one CURRENT and zero DIFFERENT and zero DIVERGING versions. (Icon: Black)*/
		CURRENT,
		
		/** A brick that has one or more DIFFERENT versions and zero DIVERGING versions. (Icon: Black with pencil)*/
		DIFFERENT,
	}
	
	String name();
	
	List<BrickVersion> versions();
	void setChosenForExecution(BrickVersion version, boolean staged);
	BrickVersion getVersionChosenForInstallation();

	Signal<Status> status();
	boolean isSnapp();

}
