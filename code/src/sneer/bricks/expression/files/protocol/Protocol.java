package sneer.bricks.expression.files.protocol;

import sneer.foundation.brickness.Brick;

/** Place holder brick for the file sharing protocol tuples. */
@Brick (hasImpl = false)
public interface Protocol {

	int FILE_BLOCK_SIZE = 10240; // 10 KB - Suitable network packet size

}
