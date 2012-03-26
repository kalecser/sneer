package sneer.installer;

import static sneer.main.SneerCodeFolders.SNEER_HOME;
import sneer.main.Sneer;

public class Main {

	public static void main(String[] args) throws Exception {
		if(!SNEER_HOME.exists())
			new InstallationWizard();

		new Installation(args[0]);
		
		overcomeWebstartSecurityRestrictions();
		new Sneer();
	}

	
	private static void overcomeWebstartSecurityRestrictions() {
		System.setSecurityManager(new PermissiveSecurityManager());
	}

}