package sneer.bricks.softwaresharing.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.BrickVersion.Status;
import sneer.bricks.softwaresharing.FileVersion;

class FakeModel {

	private static long _initialTimeStamp = System.currentTimeMillis();

	static List<BrickHistory> bricks(){
		List<FileVersion> files = new ArrayList<FileVersion>();
		List<BrickHistory> infos = new ArrayList<BrickHistory>();

		files.add(newFileVersion(FakeContent.first(), FakeContent.second(), 
				"Clockjava", sneer.bricks.softwaresharing.FileVersion.Status.DIFFERENT));

		files.add(newFileVersion("adsafimww\n222222\n3333333\n44444444\n555555", "adsafimww\n222222\n3333333\n44444444\n555555", 
				"impl/ClockImpl.java", sneer.bricks.softwaresharing.FileVersion.Status.CURRENT));

		files.add(newFileVersion("adsafimww\n222222\n3333333\n44444444\n555555", "", 
				"impl/lib/mylib.jar", sneer.bricks.softwaresharing.FileVersion.Status.MISSING));

		files.add(newFileVersion("", "adsafimww\n222222\n3333333\n44444444\n555555", 
				"impl/lib/otherlib.jar", sneer.bricks.softwaresharing.FileVersion.Status.EXTRA));

		infos.add(newBrickInfo("BrickInfo5", newVersions(files), BrickHistory.Status.DIFFERENT));
		infos.add(newBrickInfo("BrickInfo2", newVersions(files), BrickHistory.Status.NEW));
		infos.add(newBrickInfo("BrickInfo10", newVersions(files), BrickHistory.Status.REJECTED));
		infos.add(newBrickInfo("BrickInfo8", newVersions(files), BrickHistory.Status.DIVERGING));
		infos.add(newBrickInfo("BrickInfo4", newVersions(files), BrickHistory.Status.CURRENT));
		infos.add(newBrickInfo("BrickInfo7", newVersions(files), BrickHistory.Status.DIVERGING));
		infos.add(newBrickInfo("BrickInfo3", newVersions(files), BrickHistory.Status.CURRENT));
		infos.add(newBrickInfo("BrickInfo9", newVersions(files), BrickHistory.Status.REJECTED));
		infos.add(newBrickInfo("BrickInfo1", newVersions(files), BrickHistory.Status.NEW));
		infos.add(newBrickInfo("BrickInfo6", newVersions(files), BrickHistory.Status.DIFFERENT));

		return infos;
	}

	private static List<BrickVersion> newVersions(List<FileVersion> files) {
		List<BrickVersion> versions = new ArrayList<BrickVersion>();
		versions.add(newBrickVersion(Status.CURRENT, files)); 
		versions.add(newBrickVersion(Status.DIVERGING, files)); 
		versions.add(newBrickVersion(Status.DIFFERENT, files)); 
		versions.add(newBrickVersion(Status.REJECTED, files));
		return versions;
	}

	private static FileVersion newFileVersion(final String contents, final String currentContents, 
			final String fileName, final sneer.bricks.softwaresharing.FileVersion.Status status) {
		return new FileVersion(){ 
			@Override public byte[] contents() {  return contents.getBytes(); }
			@Override public byte[] contentsInCurrentVersion() { 	return currentContents.getBytes(); }
			@Override public String name() { return fileName; }
			@Override public Status status() { return status; }
			@Override public long lastModified() { return System.currentTimeMillis(); }
		};
	}

	private static BrickVersion newBrickVersion(final Status status, final List<FileVersion> _fileVersions) {
		return new BrickVersion(){ 

			private boolean _staged;
			private Register<Status> _status = my(Signals.class).newRegister(status);
			private final List<String> _users = Arrays.asList(new String[]{"User 4", "User 1", "User 3", "User 2"});
			
			@Override public List<FileVersion> files() {return _fileVersions;}
			@Override public boolean isChosenForExecution() {return _staged;}
			@Override public Signal<Status> status() { return _status.output(); }
			@Override public List<String> users() {  return  _users;}			
			
			@Override public long publicationDate() { 
				_initialTimeStamp += 1000;
				return _initialTimeStamp;	
			}
			
			@Override public void setRejected(boolean rejected) { 
				if(rejected) {
					_status.setter().consume(Status.REJECTED);
					return;
				}
				_status.setter().consume(Status.DIFFERENT);
			}
			@Override public Hash hash() {
				return null;
			}
		};
	}

	private static BrickHistory newBrickInfo(final String name, final List<BrickVersion> versions, final BrickHistory.Status status) {
		return new BrickHistory(){
			@Override public boolean isSnapp() { return false; }
			@Override public String name() {return name; }
			@Override public List<BrickVersion> versions() { return versions;}
			@Override public void setChosenForExecution(BrickVersion version, boolean staged) {}
			@Override public Signal<BrickHistory.Status> status() { return my(Signals.class).constant(status); }
			@Override public BrickVersion getVersionChosenForInstallation() { return null; }
		};
	}
}

class FakeContent{
	
	static String first(){
		return 	"" + 			"package sneer.bricks.hardware.clock;" +
		
					"\n\n" + 	"import sneer.bricks.pulp.reactive.Signal;" +
					"\n" + 		"import sneer.foundation.brickness.Brick;" +

					"\n" + 		"@Brick" +
					"\n" + 		"public interface Clock {"+
			
					"\n\n\t" + 	"void stop();" +
					"\n\t" + 	"Signal<Date> time();" +
					"\n\t" + 	"void advanceTime(long deltaMillis);" +

					"\n\n" + 	"}";
	}

	static String second(){
		return 	"" + 			"package sneer.bricks.hardware.clock;" +
		
					"\n\n" + 	"import sneer.bricks.pulp.reactive.Signal;" +
					"\n" + 		"import sneer.foundation.brickness.Brick;" +

					"\n" + 		"@Brick" +
					"\n" + 		"public interface Clock {"+
			
					"\n\n\t" + 	"Signal<Long> time();" +
					"\n\t" + 		"void advanceTimeTo(long absoluteTimeMillis);" +
					"\n\t" + 		"void advanceTime(long deltaMillis);" +

					"\n\n" + 	"}";
	}
}