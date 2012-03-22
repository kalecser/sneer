package sneer.bricks.snapps.wind;

import basis.brickness.Brick;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.snapps.chat.ChatMessage;

@Brick
public interface Wind {

	ListSignal<ChatMessage> shoutsHeard();

	Consumer<String> megaphone();

}
