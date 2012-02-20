package sneer.bricks.hardware.io.log.notifier;

import sneer.bricks.pulp.notifiers.Source;
import sneer.foundation.brickness.Brick;

@Brick
public interface LogNotifier {

	Source<String> loggedMessages();

}
