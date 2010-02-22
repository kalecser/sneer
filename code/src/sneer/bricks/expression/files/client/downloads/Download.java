package sneer.bricks.expression.files.client.downloads;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

public interface Download extends WeakContract {

	void waitTillFinished() throws IOException, TimeoutException;

}
