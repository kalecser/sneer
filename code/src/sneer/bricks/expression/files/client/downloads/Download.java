package sneer.bricks.expression.files.client.downloads;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.bricks.pulp.reactive.Signal;

public interface Download extends WeakContract {

	File file();

	Hash hash();

	Contact source();

	Signal<Integer> progress(); // Range: 0 - 100% completed

	void waitTillFinished() throws IOException, TimeoutException;

	PulseSource finished();

	boolean hasFinishedSuccessfully();

}
