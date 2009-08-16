package sneer.bricks.softwaresharing.filetobrick;

import java.util.Collection;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileToBrickConverter {

	Collection<BrickInfo> bricksInCachedSrcFolders(Collection<Sneer1024> srcFolderHashes);

}
