package sneer.bricks.hardware.io.log.filter;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.collections.ListRegister;

@Brick
public interface LogFilter{

	boolean isLogEntryAccepted(String message);
	ListRegister<String> whiteListEntries();

}
