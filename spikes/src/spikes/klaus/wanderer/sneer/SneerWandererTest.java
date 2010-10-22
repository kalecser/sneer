package spikes.klaus.wanderer.sneer;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.LoggerMocks;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class SneerWandererTest extends BrickTestBase {

	@Test
	public void wander() {
		LoggerMocks.showLog = true;
		
		Chooser chooser =  new Chooser();
		SneerCommunityWanderer wanderer = new SneerCommunityWanderer(chooser, tmpFolder());
		
		while (true)
			wanderer.wander();
	}

}
