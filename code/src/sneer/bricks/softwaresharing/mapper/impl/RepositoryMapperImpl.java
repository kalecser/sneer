package sneer.bricks.softwaresharing.mapper.impl;

import static basis.environments.Environments.my;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static sneer.bricks.pulp.blinkinglights.LightType.ERROR;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.mapper.RepositoryMapper;
import basis.lang.Closure;

public class RepositoryMapperImpl implements RepositoryMapper {

	@SuppressWarnings("unused")
	private WeakContract ref;

	{
		try {
			tryToInit();
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(ERROR, "Repository Mapper Error", "Your friends will not be able to pull code you commit to your git repository.", e);
		}
	}

	private void tryToInit() throws IOException {
		WatchService watcher = FileSystems.getDefault().newWatchService();

		Path dir = my(FolderConfig.class).gitFolder().get();
		//System.out.println(dir);
		final WatchKey key = dir.register(watcher,
			ENTRY_CREATE,
			ENTRY_DELETE,
			ENTRY_MODIFY);
		ref = my(Timer.class).wakeUpNowAndEvery(3000, new Closure() { @Override public void run() {
			key.toString();
			//System.out.println(Thread.currentThread());
			//for (WatchEvent<?> ev : key.pollEvents())
			//	System.out.println(ev);
		}});
	}
	
}
