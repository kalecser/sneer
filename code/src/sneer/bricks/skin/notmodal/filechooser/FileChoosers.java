package sneer.bricks.skin.notmodal.filechooser;

import java.io.File;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick
public interface FileChoosers {
	void choose(Consumer<File> consumer, int fileSelectionMode, File defaultFileOrDir);
	void choose(Consumer<File[]> consumer, int fileSelectionMode);
}
