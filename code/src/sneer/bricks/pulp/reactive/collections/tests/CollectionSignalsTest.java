package sneer.bricks.pulp.reactive.collections.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Functor;

public class CollectionSignalsTest extends BrickTestBase {

	private final CollectionSignals _subject = my(CollectionSignals.class);

	@Test
	public void adapt() {
		final ListRegister<Integer> numbers = _subject.newListRegister();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);

		assertArrayEquals(
			new String [] {"1", "2", "3"},
			_subject.adapt(numbers.output(), new Functor<Integer, String>() { @Override public String evaluate(Integer number) {
				return number.toString();
			}}).currentElements().toArray(new String[0])
		);
	}

}
