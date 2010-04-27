package dfcsantos.wusic.notification.playingtrack.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.notification.playingtrack.PlayingTrack;
import dfcsantos.wusic.notification.playingtrack.server.PlayingTrackPublisher;

public class PlayingTrackTest extends BrickTest {

	@Bind private final Wusic _wusic = mock(Wusic.class);
	private final Register<Track> _playingTrack = my(Signals.class).newRegister(null);

	private Contact _localContact;
	private Attributes _remoteAttributes;

	private TuplePump _tuplePump;

	@Ignore
	@Test
	public void playingTrackBroadcast() throws Exception {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrack(); will(returnValue(_playingTrack.output()));
		}});

		my(PlayingTrackPublisher.class);

		Environment remote = newTestEnvironment(my(Clock.class));
		configureStorageFolder(remote, "remote/data");

		_tuplePump = tuplePumpFor(my(Environment.class), remote);

		final Seal localSeal = my(OwnSeal.class).get().currentValue();
		Environments.runWith(remote, new ClosureX<Refusal>() { @Override public void run() throws Refusal {
			_localContact = my(Contacts.class).addContact("local");
			my(ContactSeals.class).put("local", localSeal);
			_remoteAttributes = my(Attributes.class);

			testPlayingTrack("track1");
			testPlayingTrack("track2");
			testPlayingTrack("track2");
			testPlayingTrack("track3");
			testPlayingTrack("");
			testPlayingTrack("track4");

			testNullPlayingTrack();
		}});

		crash(remote);
	}

	private void testPlayingTrack(String trackName) {
		setPlayingTrack(trackName.isEmpty() ? "" : trackName + ".mp3");
		_tuplePump.waitForAllDispatchingToFinish();
		assertEquals(trackName, playingTrackReceivedFromLocal());
	}

	private void testNullPlayingTrack() {
		my(Clock.class).advanceTime(1);
		_playingTrack.setter().consume(null);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals("", playingTrackReceivedFromLocal());
	}

	private String playingTrackReceivedFromLocal() {
		return _remoteAttributes.attributeValueFor(_localContact, PlayingTrack.class, String.class).currentValue();
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(my(Tracks.class).newTrack(new File(trackName)));
	}

	private TuplePump tuplePumpFor(Environment env1, Environment env2) {
		return new TuplePump(env1, env2);
	}

	public class TuplePump implements WeakContract {
		private final Environment _env1;
		private final Environment _env2;

		private WeakContract _toAvoidGC1;
		private WeakContract _toAvoidGC2;

		
		public TuplePump(Environment env1, Environment env2) {
			_env1 = env1;
			_env2 = env2;

			Environments.runWith(_env1, new Closure() { @Override public void run() {
				_toAvoidGC1 = my(TupleSpace.class).addSubscription(Tuple.class, pumpFor(_env2));
			}});

			Environments.runWith(_env2, new Closure() { @Override public void run() {
				_toAvoidGC2 = my(TupleSpace.class).addSubscription(Tuple.class, pumpFor(_env1));
			}});
		}

		private Consumer<Tuple> pumpFor(final Environment env) {
			return new Consumer<Tuple>() { @Override public void consume(final Tuple tuple) {
				Environments.runWith(env, new Closure() { @Override public void run() {
					my(TupleSpace.class).acquire(tuple);
				}});
			}};
		}

		public void waitForAllDispatchingToFinish() {
			Environments.runWith(_env1, new Closure() { @Override public void run() {
				my(TupleSpace.class).waitForAllDispatchingToFinish();
			}});
			
			Environments.runWith(_env2, new Closure() { @Override public void run() {
				my(TupleSpace.class).waitForAllDispatchingToFinish();
			}});
		}
		
		@Override
		public void dispose() {
			if (_toAvoidGC1 != null) {
				_toAvoidGC1.dispose();
				_toAvoidGC1 = null;
			}
			if (_toAvoidGC2 != null) {
				_toAvoidGC2.dispose();
				_toAvoidGC2 = null;
			}
		}

	}

}
