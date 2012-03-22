package sneer.bricks.hardware.io.log.notifier;

import basis.brickness.Brick;
import sneer.bricks.pulp.notifiers.Source;

@Brick
public interface LogNotifier {

	Source<String> loggedMessages();

}
