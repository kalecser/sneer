package sneer.bricks.skin.filechooser.impl;

import static basis.environments.Environments.my;

import java.io.File;

import javax.swing.JFileChooser;

import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.skin.filechooser.FileChoosers;

class FileChoosersImpl implements FileChoosers {

	@Override
	public void choose(final int fileSelectionMode, final Consumer<File[]> consumer) {
		my(Threads.class).startDaemon("Multiple Files Chooser", new Closure() {  @Override public void run() {
			JFileChooser fileChooser = newfileChooser(fileSelectionMode);
			fileChooser.setMultiSelectionEnabled(true);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				consumer.consume(fileChooser.getSelectedFiles());
		}});
	}
	
	
	@Override
	public void choose(final int fileSelectionMode, final File defaultFileOrDir, final Consumer<File> consumer) {
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