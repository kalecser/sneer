package dfcsantos.tracks;

import java.io.File;

public interface Track {

	String name();

	File file();

	void dispose();

	boolean isMarkedForDisposal();

}
