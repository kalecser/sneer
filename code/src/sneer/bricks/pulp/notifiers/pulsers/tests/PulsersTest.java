package sneer.bricks.pulp.notifiers.pulsers.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.PulseSender;
import sneer.bricks.pulp.notifiers.pulsers.PulseSenders;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class PulsersTest extends BrickTestBase {

	private final PulseSenders _subject = my(PulseSenders.class);
	private int _counter = 0;

	@Test
	public void receiveFromSeveralPulseSources() {
		PulseSender p1 = my(PulseSenders.class).newInstance();
		PulseSender p2 = my(PulseSenders.class).newInstance();
		PulseSender p3 = my(PulseSenders.class).newInstance();
		
		@SuppressWarnings("unused")
		WeakContract contract = _subject.receive(new Closure() { @Override public void run() {
			_counter++;
		}}, p1.output(), p2.output(), p3.output());
		
		p1.sendPulse();
		p2.sendPulse();
		p3.sendPulse();
		
		assertSame(3, _counter);
	}

}
