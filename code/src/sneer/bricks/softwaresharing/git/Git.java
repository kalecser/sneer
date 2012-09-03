package sneer.bricks.softwaresharing.git;

import java.nio.file.Path;

import basis.brickness.Brick;

@Brick
public interface Git {

	void pull(Path fromRepo, Path toRepo) throws Exception;

}
