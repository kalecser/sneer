package sneer.bricks.expression.files.client.downloads;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.bricks.pulp.reactive.Signal;

public interface Download extends WeakContract {

	File file();

	Sneer1024 hash();

	Contact source();

	Signal<Float> progress(); // Range: 0 - 100% completed

	void waitTillFinished() throws IOException, TimeoutException;

	PulseSource finished();

	boolean hasFinishedSuccessfully();

}
