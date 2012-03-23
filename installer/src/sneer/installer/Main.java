package sneer.installer;

import static sneer.main.SneerCodeFolders.SNEER_HOME;

import javax.swing.JOptionPane;

import sneer.main.Sneer;

public class Main {

	public static void main(String[] args) throws Exception {
		checkJava1_6();
		
		if(!SNEER_HOME.exists())
			new InstallationWizard();

		new Installation(args[0]);
		
		overcomeWebstartSecurityRestrictions();
		new Sneer();
	}

	
	private static void checkJava1_6() {
		String version = "" + System.getProperty("java.version");
		if (version.startsWith("1.6")) return;
		JOptionPane.showMessageDialog(null, "Sneer requires Java 6 to run. You are using Java " + version);
		System.exit(0);
	}


	private static void overcomeWebstartSecurityRestrictions() {
		System.setSecurityManager(new PermissiveSecurityManager());
	}

}