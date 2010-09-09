package sneer.bricks.pulp.bandwidth.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.pulp.bandwidth.BandwidthCounter;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class BandwidthConsolidationTest extends BrickTestBase {
	
	
	private final BandwidthCounter _subject = my(BandwidthCounter.class);


	@Test (timeout = 2000)
	public void bandwidthConsolidation() throws Exception {
		
		assertDownloadSpeed(0);
		assertUploadSpeed(0);

		_subject.received(1024*4);
		_subject.sent(1024*40);
		assertDownloadSpeed(0);
		assertUploadSpeed(0);

		my(Clock.class).advanceTime(4000);
		assertDownloadSpeed(1);
		assertUploadSpeed(10);

		_subject.received(1024*50);
		_subject.sent(1024*5);
		assertDownloadSpeed(1);
		assertUploadSpeed(10);

		//Implement - The following fails intermittently (timeout) because there is no way to know when a Timer alarm (used by the subject) has finished executing before calling it again. The Timer will ignore Alarms that are already running. To implement this test the Timer and the Clock have to be mocked. Do this when a decent mocking DSL has emerged. 
//		my(Clock.class).advanceTime(5000);
//		assertDownloadSpeed(10);
//		assertUploadSpeed(1);
	}

	private void assertDownloadSpeed(int expected) {
		my(SignalUtils.class).waitForValue(_subject.downloadSpeedInKBperSecond(), expected);
	}

	private void assertUploadSpeed(int expected) {
		my(SignalUtils.class).waitForValue(_subject.uploadSpeedInKBperSecond(), expected);
	}
}