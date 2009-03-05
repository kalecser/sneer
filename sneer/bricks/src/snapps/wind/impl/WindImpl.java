package snapps.wind.impl;

import static sneer.brickness.environments.Environments.my;

import java.util.Comparator;

import snapps.wind.Shout;
import snapps.wind.Wind;
import sneer.pulp.reactive.listsorter.ListSorter;
import sneer.pulp.tuples.TupleSpace;
import wheel.lang.Consumer;
import wheel.reactive.lists.ListRegister;
import wheel.reactive.lists.ListSignal;
import wheel.reactive.lists.impl.ListRegisterImpl;

class WindImpl implements Wind, Consumer<Shout> {

	private final TupleSpace _environment = my(TupleSpace.class);
	
	private final ListSorter _sorter = my(ListSorter.class);
	
	private final ListSignal<Shout> _sortedShouts;
	private final Comparator<Shout> _comparator;
	private final ListRegister<Shout> _shoutsHeard = new ListRegisterImpl<Shout>();

	WindImpl(){
		_environment.addSubscription(Shout.class, this);
		_environment.keep(Shout.class);
		
		_comparator = new Comparator<Shout>(){ @Override public int compare(Shout o1, Shout o2) {
			return (int) (o1.publicationTime() - o2.publicationTime());
		}};
		
		_sortedShouts = _sorter.sort(_shoutsHeard.output(), _comparator);
	}

	@Override
	public ListSignal<Shout> shoutsHeard() {
		return _sortedShouts;
	}

	@Override
	public void consume(Shout shout) {
		_shoutsHeard.adder().consume(shout);
	}

	@Override
	public Consumer<String> megaphone() {
		return new Consumer<String>(){ @Override public void consume(String phrase) {
			shout(phrase);
		}};
	}

	private void shout(String phrase) {
		_environment.publish(new Shout(phrase));
	}
}