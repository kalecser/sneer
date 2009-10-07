package sneer.bricks.network.social.heartbeat.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.network.social.heartbeat.Heart;
import sneer.bricks.network.social.heartbeat.Heartbeat;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.ByRef;

public class HeartTest extends BrickTest {

	@Bind TupleSpace _tupleSpace = mock(TupleSpace.class);
	@Bind Timer _timer = mock(Timer.class);

	@Test
	public void heartIsBeating() {
		
		final ByRef<Runnable> _timerSteppable = ByRef.newInstance();
		
		checking(new Expectations() {{
			allowing(_timer).wakeUpNowAndEvery(with(any(Long.class)), with(any(Runnable.class)));
			will(new CustomAction("Run timer runnables") { @Override public Object invoke(Invocation invocation) throws Throwable {
				assertEquals(10000L, invocation.getParameter(0));
				_timerSteppable.value = (Runnable)invocation.getParameter(1); return null;
			}});

			exactly(2).of(_tupleSpace).publish(with(any(Heartbeat.class)));
		}});

		my(Heart.class);
		_timerSteppable.value.run();
		_timerSteppable.value.run();
	}

}
