package sneer.bricks.expression.files.client.downloads;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Signal;

public interface Download extends WeakContract {

	File file();

	Hash hash();

	Seal source();

	Signal<Integer> progress(); // Range: 0 - 100% completed

	void waitTillFinished() throws IOException, TimeoutException;

	Signal<Boolean> finished();

	boolean hasFinishedSuccessfully();

	void onFinished(Runnable action);

}
