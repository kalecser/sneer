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
	public void choose(final Consumer<File[]> consumer, final int fileSelectionMode) {
		my(Threads.class).startDaemon("Multiple File Chooser", new Closure() {  @Override public void run() {
			JFileChooser fileChooser = newfileChooser(fileSelectionMode);
			fileChooser.setMultiSelectionEnabled(true);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				consumer.consume(fileChooser.getSelectedFiles());
		}});
	}
	
	
	@Override
	public void choose(final Consumer<File> consumer, final int fileSelectionMode, final File defaultFileOrDir) {
		my(Threads.class).startDaemon("File Chooser", new Closure() {  @Override public void run() {
			JFileChooser fileChooser = newfileChooser(fileSelectionMode, defaultFileOrDir);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				consumer.consume(fileChooser.getSelectedFile());
		}});
	}
	
	
	synchronized
	private JFileChooser newfileChooser(int fileSelectionMode, final File defaultFileOrDir) {
		JFileChooser fileChooser = newfileChooser(fileSelectionMode);
		fileChooser.setCurrentDirectory(defaultFileOrDir);
		return fileChooser;
	}

	
	synchronized
	private JFileChooser newfileChooser(int fileSelectionMode) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(fileSelectionMode);
		return fileChooser;
	}
	
}