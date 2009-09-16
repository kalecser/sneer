package dfcsantos.wusic.impl;

import java.io.File;

public class Track{
	private File _file;
	
	private String _info;
	
	public Track(File file){
		_file = file;
		_info = file.getName();
		_info = _info.replaceAll(".mp3", "");
	}
	
	public String info(){
		return _info; 
	}
	
	public File file(){
		return _file;
	}
}
