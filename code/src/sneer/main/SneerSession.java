package sneer.main;

import static basis.environments.Environments.my;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import sneer.bricks.identity.keys.gui.PublicKeyInitDialog;
import sneer.bricks.software.bricks.snapploader.SnappLoader;

public class SneerSession extends SneerSessionBase {
	
	@Override
	protected void start() {
		setLookAndFeel();
		my(PublicKeyInitDialog.class);
		my(SnappLoader.class);
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
}
