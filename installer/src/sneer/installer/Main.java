package sneer.installer;

import static sneer.main.SneerCodeFolders.SNEER_HOME;
import sneer.main.Sneer;

public class Main {

	public static void main(String[] args) throws Exception {
		System.setProperty("sneer.home", System.getProperty("user.home"));
		if(!SNEER_HOME.exists())
			new InstallationWizard();

		new Installation(args[0]).run();
		
		overcomeWebstartSecurityRestrictions();
		new Sneer();
	}

	
	private static void overcomeWebstartSecurityRestrictions() {
		System.setSecurityManager(new PermissiveSecurityManager());
	}

}