package sneer.bricks.skin.main.synth.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.InputStream;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.skin.main.synth.Synth;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;

class SynthImpl implements Synth {
	
	private final LookAndFeel _jgoodies = new Plastic3DLookAndFeel();
	private final SynthLookAndFeel _synth = new SynthLookAndFeel();
	private final MetalLookAndFeel _default = new MetalLookAndFeel();
	
	SynthImpl(){
		try {
			PlasticLookAndFeel.setPlasticTheme(new LightGray());
			UIManager.setLookAndFeel(_jgoodies);
			load(SynthImpl.class);
			UIManager.setLookAndFeel(_default);
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}
	

	@Override
	public void load(final Class<?> resourceBase){
		my(GuiThread.class).assertInGuiThread();
		InputStream is = null;
		try {
			is = resourceBase.getResourceAsStream("synth.xml");
			_synth.load(is, resourceBase);
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} finally {
			try { is.close(); } catch (Exception e2) { /* ignore */ }
		}	
	}
	
	@Override
	public Icon load(final Class<?> resourceBase, final String resourceName){
		my(GuiThread.class).assertInGuiThread();
		URL path = resourceBase.getResource(resourceName);
		return new ImageIcon(path);	
	}

	
	@Override
	public void attach(final JComponent component) {
		my(GuiThread.class).assertInGuiThread();
		try {
			UIManager.setLookAndFeel(_synth);
			SwingUtilities.updateComponentTreeUI(component);
			UIManager.setLookAndFeel(_default);
		} catch (UnsupportedLookAndFeelException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}			
	}

	
	@Override
	public void attach(JComponent component, String synthName) {
		attach(component);
		component.setName(synthName);
	}

}