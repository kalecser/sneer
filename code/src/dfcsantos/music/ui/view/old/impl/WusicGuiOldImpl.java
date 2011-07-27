package dfcsantos.music.ui.view.old.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.snapps.contacts.gui.ContactTextProvider;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;
import dfcsantos.music.Music;
import dfcsantos.music.notification.playingtrack.PlayingTrack;
import dfcsantos.music.ui.view.old.WusicGuiOld;
import dfcsantos.tracks.Track;



class WusicGuiOldImpl implements WusicGuiOld {

	private static final String MUSICAL_NOTE_ICON = "\u266A"; 

    private static final Music Wusic = my(Music.class);

    private JFrame _frame;

    {
		my(MainMenu.class).menu().addAction(30, "Wusic", new Closure() { @Override synchronized public void run() {
			if (_frame == null)
				_frame = initFrame();
			_frame.setVisible(true);
		}});

		registerPlayingTrackTextProvider();
	}

	private JFrame initFrame() {
		JFrame result = my(ReactiveWidgetFactory.class).newFrame(title()).getMainWidget();

		result.add(new MainPanel());
		// Implement: Set location of Wusic's frame relative to the Dashboard using the WindowBoundSetter
		result.setLocationRelativeTo(null);
		result.pack();

		return result;
	}

	private Signal<String> title() {
		return my(Signals.class).adapt(Wusic.playingTrack(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return "Wusic :: " + (track == null ? "" : track.name());
		}});
	}

	private void registerPlayingTrackTextProvider() {
		my(ContactsGui.class).registerContactTextProvider(
			new ContactTextProvider() {
				@Override public Position position() {
					return ContactTextProvider.Position.RIGHT; 
				}

				@Override
				public Signal<String> textFor(Contact contact) {
					return my(Signals.class).adapt(my(Attributes.class).attributeValueFor(contact, PlayingTrack.class, String.class), new Functor<String, String>() { @Override public String evaluate(String playingTrack) throws RuntimeException {
						return (playingTrack == null || playingTrack.isEmpty()) ? "" : MUSICAL_NOTE_ICON + " " + playingTrack;
					}});
				}
			}
		);
	}

}
