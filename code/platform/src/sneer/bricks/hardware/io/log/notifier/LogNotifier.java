package sneer.bricks.hardware.io.log.notifier;

import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.brickness.Brick;

@Brick
public interface LogNotifier {

	EventSource<String> loggedMessages();

}
