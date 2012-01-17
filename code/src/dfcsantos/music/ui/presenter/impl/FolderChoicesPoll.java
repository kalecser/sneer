package dfcsantos.music.ui.presenter.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderChoicesPoll {
	
	private final File shared; 
	
	
	public FolderChoicesPoll(File shared) {
		this.shared = shared;
	}
	
	
	public List<String> result() {
		List<String> subFordersPaths = new ArrayList<String>();
		if (shared != null)
			loadFolder(shared.getAbsolutePath(), shared, subFordersPaths);
		return subFordersPaths;
	}
	
	
	private void loadFolder(final String sharedTracksFolderPath, File folder, List<String> subFordersPaths) {
		if (folder == null) return;
		if (folder.isFile()) return;
		
		loadFolderPath(sharedTracksFolderPath, folder.getAbsolutePath(), subFordersPaths);
			
		for (File subFolder : folder.listFiles())
			loadFolder(sharedTracksFolderPath, subFolder, subFordersPaths);
	}

	
	private void loadFolderPath(String sharedTracksFolderPath, String subFolderPath, List<String> subFordersPaths) {
		 if (subFolderPath == null) return;
		 if (subFolderPath.equals(sharedTracksFolderPath)) return;
		 String result = subFolderPath.replace(sharedTracksFolderPath + File.separator, "");
		 subFordersPaths.add(result);
	}

}