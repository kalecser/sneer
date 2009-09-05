package sneer.bricks.pulp.dyndns.ownip.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.pulp.dyndns.checkip.CheckIp;
import sneer.bricks.pulp.dyndns.ownip.OwnIpDiscoverer;
import sneer.bricks.pulp.propertystore.PropertyStore;
import sneer.bricks.pulp.propertystore.mocks.TransientPropertyStore;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;

public class OwnIpDiscovererTest extends TestThatUsesLogger {
	
	@Bind private final CheckIp _checkip = mock(CheckIp.class);
	@Bind private final Timer _timer = mock(Timer.class);
	@SuppressWarnings("unused") @Bind private final PropertyStore _store = new TransientPropertyStore();

	private long _retryTime;
	private Runnable _timerRunnable;
	
	@Test
	public void testDiscovery() throws IOException {
		final Consumer<String> receiver = mock(Consumer.class);
		final String ip1 = "123.45.67.89";
		final String ip2 = "12.34.56.78";

		checking(new Expectations() {{

			final Sequence seq = newSequence("Sequence");
			
			allowing(_timer).wakeUpNoEarlierThan(with(any(Long.class)), with(any(Runnable.class)));
				will(new CustomAction("Capturing runnable") { @Override public Object invoke(Invocation invocation) throws Throwable {
					assertEquals(_retryTime, invocation.getParameter(0));
					_timerRunnable = (Runnable)invocation.getParameter(1);
					return null;
				}});

			one(receiver).consume(null); inSequence(seq);

			one(_checkip).check(); will(returnValue(ip1)); inSequence(seq);
			one(receiver).consume(ip1); inSequence(seq);
			
			one(_checkip).check(); will(returnValue(ip1)); inSequence(seq);
			
			one(_checkip).check(); will(returnValue(ip2)); inSequence(seq);
			one(receiver).consume(ip2); inSequence(seq);
			
			one(_checkip).check(); will(returnValue(ip2)); inSequence(seq);
		}});
		
		_retryTime = 0;
		OwnIpDiscoverer subject = my(OwnIpDiscoverer.class);

		@SuppressWarnings("unused")
		final WeakContract contract = subject.ownIp().addReceiver(receiver);

		_retryTime = 11 * 60 * 1000;
		_timerRunnable.run();
		_timerRunnable.run();
		_timerRunnable.run();
		_timerRunnable.run();
	}

}