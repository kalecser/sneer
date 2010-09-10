package sneer.bricks.snapps.wind;

import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick
public interface Wind {

	ListSignal<ChatMessage> shoutsHeard();

	Consumer<String> megaphone();

}
