package dfcsantos.music.ui.view.impl;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.bricks.skin.menu.MenuGroup;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;



class MusicViewImpl implements MusicView {

	public static void main(String[] args) {
			final JFrame jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			MusicInstrument instrument = new MusicInstrument(null);
			instrument.init(new InstrumentPanel() {
				@Override public Container contentPane() { return jFrame.getContentPane(); }
				@Override public MenuGroup<JPopupMenu> actions() { return null; }
			});
			jFrame.setBounds(100, 100, 200, instrument.defaultHeight());
			jFrame.pack();
			jFrame.setVisible(true);
	}


	private boolean alreadyInitialized;

	
	@Override
	public Instrument initInstrument(MusicViewListener listener) {
		if (alreadyInitialized) throw new IllegalStateException();
		alreadyInitialized = true;
		return new MusicInstrument(listener);
	}

}
