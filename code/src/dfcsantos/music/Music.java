/*

Use cases:

Set tracks folder. - Abre no início um FileChooser - Opcao de menu
Exchange Tracks on/off - Default on - Opcao de menu
See downloads in progress - Opcao de menu

Listen - Any track
	Name of track being played. Time ellapsed.
	Play/Pause, Next, Stop
	Volume (vertical)
	Delete file of track being played.
		:P    (...but I like it. Bring me similar ones.)
		:(    (...don't bring similar tracks. (Today's "No Way"))
	
Listen - Own tracks
	Choose a folder (drop down?)
	Choose a song (autocomplete? future)
	Shuffle - Toggle on/off

Listen - Downloaded for peers
	:D  - I want this track. (Today's "Me Too")
 
*/


package dfcsantos.music;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface Music {

	enum OperatingMode { OWN, PEERS };
	void setOperatingMode(OperatingMode operatingMode);
	Signal<OperatingMode> operatingMode();

	File playingFolder();
	void setPlayingFolder(File selectedFolder);

	Signal<File> tracksFolder();
	void setTracksFolder(File selectedFolder);

	void pauseResume();
	void skip();
	void stop();
	Register<Integer> volumePercent();
	Register<Boolean> shuffle();

	void meToo();
	void deleteTrack();

	Signal<Boolean> isPlaying();
	Signal<Track> playingTrack();
	Signal<Integer> playingTrackTime();

	Signal<Integer> numberOfOwnTracks();
	Signal<Integer> numberOfPeerTracks();

	Register<Boolean> isTrackExchangeActive();
	SetSignal<Download> activeDownloads();

}