package sneer.bricks.softwaresharing;

import java.util.List;

public interface BrickHistory {
	
	public enum Status {
		/** A brick that has only rejected versions. */
		REJECTED,

		/** A brick that has one or more DIVERGING versions.*/
		DIVERGING,
		
		/** A brick that has no CURRENT version and has one or more DIFFERENT versions. */
		NEW,
		
		/** A brick that has one CURRENT and zero DIFFERENT and zero DIVERGING versions. */
		CURRENT,
		
		/** A brick that has one or more DIFFERENT versions and zero DIVERGING versions. */
		DIFFERENT,
	}
	
	String name();
	
	List<BrickVersion> versions();
	void setStagedForInstallation(BrickVersion version, boolean staged);
	BrickVersion getVersionStagedForInstallation();

	Status status();
	boolean isSnapp();

}
