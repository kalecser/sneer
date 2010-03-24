package sneer.bricks.expression.files.client.downloads;

import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.events.pulsers.PulseSource;

public interface Download extends WeakContract {

	Sneer1024 hash();

	void waitTillFinished() throws IOException, TimeoutException;

	PulseSource finished();

	boolean hasFinishedSuccessfully();

}
