
package sneer.bricks.software.bricks.finder;

import java.io.IOException;
import java.util.Collection;

import basis.brickness.Brick;


@Brick
public interface BrickFinder {
	
	Collection<String> findBricks() throws IOException;

}
