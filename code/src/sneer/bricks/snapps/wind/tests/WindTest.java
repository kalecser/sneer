package sneer.bricks.snapps.wind.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.wind.Wind;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class WindTest extends BrickTestBase {

	private static final long YEAR_ONE = 1000L * 60L * 60L * 24L * 356L;
	private final Wind _subject = my(Wind.class);
	
	@Test(timeout = 4000)
	public void oldShoutsAreNotHeard() {
		ChatMessage ahhh = new ChatMessage("AHHH!!!");

		my(Clock.class).advanceTimeTo(YEAR_ONE);
		tupleSpace().add(ahhh);

		ChatMessage choo = new ChatMessage("CHOOO!!!");
		tupleSpace().add(choo);

		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		
		assertTrue(_subject.shoutsHeard().currentElements().contains(choo));
		assertEquals(1, _subject.shoutsHeard().currentElements().size());
	}

	
	@Test(timeout = 4000)
	public void testSortedShoutsHeard() {
		my(Clock.class).advanceTimeTo(15);
		tupleSpace().add(new ChatMessage(""+15));

		for (int i = 30; i > 20; i--) {
			my(Clock.class).advanceTimeTo(i);
			tupleSpace().add(new ChatMessage(""+i));
		}
		
		for (int i = 10; i > 0; i--) {
			my(Clock.class).advanceTimeTo(i);
			tupleSpace().add(new ChatMessage(""+i));
		}

		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		
		ChatMessage previousShout = null;
		for (ChatMessage shout : _subject.shoutsHeard()) {
			
			if (previousShout == null) {
				previousShout = shout;
				continue;
			}
			
			assertTrue(previousShout.publicationTime < shout.publicationTime);
			previousShout = shout;
		}

		assertEquals(21, _subject.shoutsHeard().size().currentValue().intValue());
	}

	private TupleSpace tupleSpace() {
		return my(TupleSpace.class);
	}
}