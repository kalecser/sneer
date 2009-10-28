package dfcsantos.tracks;

import java.io.File;

import sneer.bricks.pulp.crypto.Sneer1024;

public interface Track {

	String name();

	File file();

	Sneer1024 hash();

	long lastModified();

}
