package sneer.bricks.snapps.wackup.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.bricks.pulp.events.pulsers.Pulser;
import sneer.bricks.pulp.events.pulsers.Pulsers;
import sneer.bricks.snapps.wackup.NewFile;
import sneer.bricks.snapps.wackup.Wackup;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.arrays.ImmutableByteArray;


class WackupImpl implements Wackup {

	private Pulser _newFileArrived=my(Pulsers.class).newInstance();
	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGC;
	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGC2;

	{
		_refToAvoidGC2 = my(RemoteTuples.class).addSubscription(NewFile.class, new Consumer<NewFile>() {

			@Override
			public void consume(NewFile newFile) {
				onNewFile(newFile);
			}
		});
		
		_refToAvoidGC = my(Timer.class).wakeUpNowAndEvery(60*1000, new Runnable() {

			@Override
			public void run() {
				poll();
			}});
	}
	
	@Override
	public File folder() {
		return my(FolderConfig.class).storageFolderFor(Wackup.class);
	}

	protected void onNewFile(NewFile newFile) {
		try {
		//	System.out.println("OnNewFile " + newFile);
			my(IO.class).files().writeByteArrayToFile(new File(folder(), newFile._name), newFile._content.copy());
			_newFileArrived.sendPulse();
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	protected void poll() {
		// System.out.println("Polling..." + folder());
		if (folder().listFiles().length == 0) return;
		try {
			publish(folder().listFiles()[0]);
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	private void publish(File file) throws IOException {
		// System.out.println("Publish " + file);
		byte[] content = my(IO.class).files().readBytes(file);
		my(TupleSpace.class).acquire(new NewFile(file.getName(), new ImmutableByteArray(content)));
	}

	@Override
	public PulseSource newFileArrived() {
		return _newFileArrived.output();
	}

}
