package sneer.bricks.skin.notmodal.filechooser.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class FileChoosersImpl implements FileChoosers {

	@Override
	public JFileChooser newFileChooser(Consumer<File> selectionReceiver) {
		return new JFileChooser();
	}

	
	@Override
	public void choose(final Consumer<File> consumer, final int fileSelectionMode, final File defaultFileOrDir) {
		my(Threads.class).startDaemon("File Chooser", new Closure() {  @Override public void run() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(fileSelectionMode);
			fileChooser.setCurrentDirectory(defaultFileOrDir);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) ;
				consumer.consume(fileChooser.getSelectedFile());
		}});
	}
}