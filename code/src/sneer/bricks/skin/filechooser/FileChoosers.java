package sneer.bricks.skin.filechooser;

import java.io.File;

import basis.brickness.Brick;
import basis.lang.Consumer;


@Brick
public interface FileChoosers {
	void choose(Consumer<File> consumer, int fileSelectionMode, File defaultFileOrDir);
	void choose(Consumer<File[]> consumer, int fileSelectionMode);
}
