package dfcsantos.music.ui.view.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;

import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuGroup;
import dfcsantos.music.ui.view.MusicView;
import dfcsantos.music.ui.view.MusicViewListener;


class MusicViewDemo {

	public static void main(String[] args) throws Exception {
		setLookAndFeel("Nimbus");
		Environments.runWith(Brickness.newBrickContainer(), new Closure() {  @Override public void run() {
			demo();
		}});
	}


	private static void setLookAndFeel(String laf) throws Exception {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			if (laf.equals(info.getName()))
	            UIManager.setLookAndFeel(info.getClassName());
	}


	private static void demo() {
		my(TimeboxedEventQueue.class).startQueueing(10000);

		final JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel instrumentPanel = new JPanel();
		
		jFrame.setLayout(new BorderLayout());
		jFrame.add(my(MainMenu.class).getMenuBarWidget(), BorderLayout.NORTH);
		jFrame.add(instrumentPanel, BorderLayout.CENTER);
		
		my(MusicView.class).setListener(new MusicViewListener() {

			private Register<Boolean> isExchangingTracks = my(Signals.class).newRegister(true);
			private Register<Integer> volumePercent = my(Signals.class).newRegister(50);
			private Register<Boolean> shuffle = my(Signals.class).newRegister(true);
			private Signal<Boolean> playing = my(Signals.class).constant(false);
			private Signal<String> trackName = my(Signals.class).constant("Here Comes The Sun");
			private Signal<Integer> trackTime = my(Signals.class).constant(111620);
			private Signal<String> _choiceSelected = my(Signals.class).constant(null);
			private Signal<Boolean> meTooEnable = my(Signals.class).constant(false);
			private Signal<Boolean> trackDownloadedEnable = my(Signals.class).constant(false);
			private ListRegister<String> playingFolderChoices = my(CollectionSignals.class).newListRegister();
			{ playingFolderChoices.adder().consume("<Inbox> 0 Tracks"); }
			@SuppressWarnings("unused")
			private WeakContract refToAvoidGc = my(Timer.class).wakeUpNowAndEvery(1000 * 5, new Runnable() {  @Override public void run() {
				System.out.println("added");
				for (int i = 0; i < 50; i++)
					playingFolderChoices.add("pasta" + i + System.currentTimeMillis());
				playingFolderChoices.removeAt(1);
			}});
			
			@Override public void chooseTracksFolder() {}
			@Override public void pauseResume() { }
			@Override public void skip() { }
			@Override public void stop() { }
			@Override public void meh() { }
			@Override public void meToo() { }
			@Override public void noWay() { }
			@Override public Register<Integer> volumePercent() { return volumePercent; }
			@Override public Register<Boolean> isTrackExchangeActive() { return isExchangingTracks; }
			@Override public Register<Boolean> shuffle() { return shuffle; }
			@Override public Signal<Boolean> isPlaying() { return playing; }
			@Override public Signal<String> playingTrackName() { return trackName; }
			@Override public Signal<Integer> playingTrackTime() { return trackTime; }
			@Override public void playingInboxFolder() {}
			@Override public void playingFolderChosen(String chosenFolder) { }

			@Override public Signal<String> choiceSelected() { return _choiceSelected; }
			@Override public Signal<Boolean> enableMeToo() { return meTooEnable; }
			@Override public Signal<Boolean> enableTrackDownloaded() { return trackDownloadedEnable; }
			@Override public ListSignal<String> playingFolderChoices() {
				return playingFolderChoices.output();
			}
			@Override
			public String playingFolder() {
				return playingFolderChoices.output().currentGet(0);
			}
			
		});
		my(MusicView.class).init(new InstrumentPanel() {
			@Override public Container contentPane() { return instrumentPanel; }
			@Override public MenuGroup<? extends JComponent> actions() { return my(MainMenu.class).menu(); }
		});
		jFrame.setBounds(100, 100, 200, my(MusicView.class).defaultHeight());
		jFrame.pack();
		jFrame.setVisible(true);
		
		my(ClockTicker.class);
	}
}
