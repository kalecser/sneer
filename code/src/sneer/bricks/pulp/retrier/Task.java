package sneer.bricks.pulp.retrier;

import basis.lang.exceptions.FriendlyException;
import sneer.bricks.hardware.cpu.exceptions.Hiccup;

public interface Task {

	void execute() throws FriendlyException, Hiccup;

}
