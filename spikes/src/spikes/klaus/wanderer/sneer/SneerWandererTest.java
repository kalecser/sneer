package spikes.klaus.wanderer.sneer;

import java.util.Random;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class SneerWandererTest extends BrickTestBase {

	@Test
	public void wander() {
		Random random = new Random(0);
		SneerCommunityWanderer wanderer = new SneerCommunityWanderer(tmpFolder());
		
		while (true)
			wanderer.wanderAt(random);
	}

}
