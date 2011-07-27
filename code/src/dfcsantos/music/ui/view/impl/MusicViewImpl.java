package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuGroup;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;



class MusicViewImpl implements MusicView {

	public static void main(String[] args) {
		Environments.runWith(Brickness.newBrickContainer(), new Closure() {  @Override public void run() {
			demo();
		}});
	}


	private static void demo() {
		final JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel instrumentPanel = new JPanel();
		
		jFrame.setLayout(new BorderLayout());
		jFrame.add(my(MainMenu.class).getMenuBarWidget(), BorderLayout.NORTH);
		jFrame.add(instrumentPanel, BorderLayout.CENTER);
		
		MusicInstrument instrument = new MusicInstrument(new MusicViewListener() {
			
			@Override
			public void toggleTrackExchange() {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
			}
			
			@Override
			public Signal<Boolean> isExchangingTracks() {
				return my(Signals.class).constant(false);
			}
			
			@Override
			public void chooseTracksFolder() {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
			}
		});
		instrument.init(new InstrumentPanel() {
			@Override public Container contentPane() { return instrumentPanel; }
			@Override public MenuGroup<? extends JComponent> actions() { return my(MainMenu.class).menu(); }
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
