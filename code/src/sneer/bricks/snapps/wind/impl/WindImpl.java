package sneer.bricks.snapps.wind.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Comparator;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.collections.listsorter.ListSorter;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.wind.Wind;
import sneer.foundation.lang.Consumer;

class WindImpl implements Wind, Consumer<ChatMessage> {

	private final ListSignal<ChatMessage> _sortedShouts;
	private final ListRegister<ChatMessage> _shoutsHeard = my(CollectionSignals.class).newListRegister();
	@SuppressWarnings("unused")	private final WeakContract _tupleSpaceContract;

	WindImpl(){
		_tupleSpaceContract = my(TupleSpace.class).addSubscription(ChatMessage.class, this);
		my(TupleSpace.class).keep(ChatMessage.class);
		
		_sortedShouts = my(ListSorter.class).sort(_shoutsHeard.output(), new Comparator<ChatMessage>(){ @Override public int compare(ChatMessage o1, ChatMessage o2) {
			return (int) (o1.publicationTime - o2.publicationTime);
		}});
	}

	@Override
	public ListSignal<ChatMessage> shoutsHeard() {
		return _sortedShouts;
	}

	@Override
	public void consume(ChatMessage shout) {
		if (my(Clock.class).time().currentValue() - shout.publicationTime > 1000 * 60 * 60 * 24) return;
		_shoutsHeard.adder().consume(shout);
	}

	@Override
	public Consumer<String> megaphone() {
		return new Consumer<String>(){ @Override public void consume(String phrase) {
			shout(phrase);
		}};
	}

	private void shout(String phrase) {
		my(Logger.class).log("Shouting: " + phrase);
		my(TupleSpace.class).acquire(new ChatMessage(phrase));
	}
}