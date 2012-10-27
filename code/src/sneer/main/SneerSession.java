package sneer.main;

import static basis.environments.Environments.my;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import sneer.bricks.identity.keys.gui.PublicKeyInitDialog;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.bricks.compiler.BrickCompilerException;
import sneer.bricks.software.bricks.compiler.Builder;
import sneer.bricks.software.bricks.snapploader.SnappLoader;
import sneer.bricks.software.code.compilers.scala.ScalaCompiler;
import sneer.bricks.software.folderconfig.FolderConfig;

public class SneerSession extends SneerSessionBase {
	
	@Override
	protected void start() {
		setLookAndFeel();
		compileScalaBricks();
		my(SnappLoader.class);
	}

	private void compileScalaBricks() {
		my(PublicKeyInitDialog.class);
		try {
			my(ScalaCompiler.class);
			my(Builder.class).build(my(FolderConfig.class).srcFolder().get(), my(FolderConfig.class).binFolder().get());
		} catch (BrickCompilerException | IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Compile error", "Scala brick compilation error", e);
		}
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
