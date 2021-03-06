package sneer.bricks.network.social.heartbeat.tests;

import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import basis.brickness.testsupport.Bind;
import basis.lang.ByRef;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.network.social.heartbeat.Heart;
import sneer.bricks.network.social.heartbeat.Heartbeat;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class HeartTest extends BrickTestBase {

	@Bind TupleSpace _tupleSpace = mock(TupleSpace.class);
	@Bind Timer _timer = mock(Timer.class);

	@Test (timeout = 2000)
	public void heartIsBeating() {
		
		final ByRef<Runnable> _timerSteppable = ByRef.newInstance();
		
		checking(new Expectations() {{
			allowing(_timer).wakeUpEvery(with(any(Long.class)), with(any(Runnable.class))); //Fix: Delete this line and find a better way. This breaks encapsulation too much.

			allowing(_timer).wakeUpNowAndEvery(with(any(Long.class)), with(any(Runnable.class)));
			will(new CustomAction("Run timer runnables") { @Override public Object invoke(Invocation invocation) throws Throwable {
				Runnable runnable = (Runnable)invocation.getParameter(1);
				assertEquals(10000L, invocation.getParameter(0));
				_timerSteppable.value = runnable; return null;
			}});

			exactly(2).of(_tupleSpace).add(with(any(Heartbeat.class)));
		}});

		my(Heart.class);
		_timerSteppable.value.run();
		_timerSteppable.value.run();
	}

}
