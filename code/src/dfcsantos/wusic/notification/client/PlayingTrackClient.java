package dfcsantos.wusic.notification.client;

import dfcsantos.wusic.notification.protocol.PlayingTrack;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Snapp
@Brick
public interface PlayingTrackClient extends Consumer<PlayingTrack> {}
