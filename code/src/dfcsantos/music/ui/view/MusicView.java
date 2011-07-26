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




package dfcsantos.music.ui.view;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.foundation.brickness.Brick;

@Brick(GUI.class)
public interface MusicView extends Instrument {}
