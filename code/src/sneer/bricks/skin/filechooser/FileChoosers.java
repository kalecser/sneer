package sneer.bricks.skin.filechooser;

import java.io.File;

import basis.brickness.Brick;
import basis.lang.Consumer;


@Brick
public interface FileChoosers {
	void choose(int fileSelectionMode, File defaultFileOrDir, Consumer<File> consumer);
	void choose(int fileSelectionMode, Consumer<File[]> consumer);
}
