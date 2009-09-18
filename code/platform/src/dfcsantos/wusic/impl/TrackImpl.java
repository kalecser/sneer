package dfcsantos.wusic.impl;

import java.io.File;

import dfcsantos.wusic.Track;

class TrackImpl implements Track {
	private final File _file;
	
	private final String _info;
	
	TrackImpl(File file){
		_file = file;
		_info = file.getName().replaceAll(".mp3", "");
	}
	
	@Override
	public String info() {
		return _info; 
	}
	
	@Override
	public File file(){
		return _file;
	}
}
