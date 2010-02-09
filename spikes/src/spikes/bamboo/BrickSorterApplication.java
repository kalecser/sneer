package spikes.bamboo;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.CachingEnvironment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.exceptions.NotImplementedYet;
import spikes.bamboo.bricksorter.BrickSorter;

public class BrickSorterApplication {

	public static void main(String[] args) throws IOException {
		Environments.runWith(new CachingEnvironment(Brickness.newBrickContainer()), new ClosureX<IOException>() { public void run() throws IOException {
			List<Class<?>> originalBricks = new ArrayList<Class<?>>();
			for (Class<?> brick : bricksToSortAccordingToDependencies())
				originalBricks.add(brick);
			
			for (Class<?> brick : my(BrickSorter.class).sort(originalBricks.toArray(new Class<?>[] {})))
				if (originalBricks.contains(brick))
					System.out.println(brick.getName() + ".class,");				
		}});
	}

	private static Class<?>[] bricksToSortAccordingToDependencies() {
		throw new NotImplementedYet("You just have to return the bricks you want sorted according to dependencies...");
	}

}
