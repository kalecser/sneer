package sneer.bricks.pulp.reactive.collections.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Functor;

public class CollectionSignalsTest extends BrickTestBase {

	private final CollectionSignals _subject = my(CollectionSignals.class);

	@Test
	public void adapt() {
		final SetRegister<Integer> numbers = _subject.newSetRegister();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);

		assertContentsInAnyOrder(
			Arrays.asList("1", "2", "3"),
			_subject.adapt(numbers.output(), new Functor<Integer, String>() { @Override public String evaluate(Integer number) {
				return number.toString();
			}}).currentElements().toArray(new String[0])
		);
	}

}
