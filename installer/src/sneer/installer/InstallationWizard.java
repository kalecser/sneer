package sneer.installer;

import static sneer.main.SneerCodeFolders.SNEER_HOME;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


public class InstallationWizard extends JFrame {

	private final String WIZARD_TITLE = "Sneer Installation Wizard";

	InstallationWizard() throws Exception {
		setLookAndFeel();
		
		welcome();
		license();
		configInformation();
		new Installation();
	}

	private void welcome() {
		showDialog(
		"Welcome to Sneer, the first sovereign computing peer.  :)\n\n" +
		"This wizard will prepare Sneer to run for you.", 
		
		"Whatever >"); 
	}

	private void license() {
		showDialog(
		"Sneer is free software.\n\n" +
		"It is licensed under the terms of the GNU Affero General Public License\n" +
		"version 3 as published by the Free Software Foundation:\n" +
		"http://www.fsf.org/licensing/licenses/agpl-3.0.html\n\n" +
		"Do you accept these terms?", 
		
		"I Accept >","I Do Not Accept"); 		
	}
	
	private void configInformation() {
		showDialog(
		"Each user of this computer can have his own Sneer setup.\n\n" +
		"To store your setup, the following folder will be created:\n" +
		SNEER_HOME.getAbsolutePath(), 
		
		"Whatever >");
	}
	
	private void showDialog(String msg, Object...options) {
		Dialogs.show(WIZARD_TITLE, msg,	exitDialog(), options);
	}

	private Runnable exitDialog() {
		return new Runnable() { @Override public void run() {
			Dialogs.show(WIZARD_TITLE, "This wizard will now exit with no changes to your system.", systemExit(), "Exit");
			System.exit(0);
		}};
	}

	private Runnable systemExit() {
		return new Runnable() { @Override public void run() {
			System.exit(0);
		}};
	}

	private static void setLookAndFeel() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		        if ("Nimbus".equals(info.getName()))
		            UIManager.setLookAndFeel(info.getClassName());
		} catch (Exception e) {
			// Default look and feel will be used.
		}
	}

	private static final long serialVersionUID = 1L;
}