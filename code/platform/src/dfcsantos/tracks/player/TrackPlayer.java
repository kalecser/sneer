package dfcsantos.tracks.player;

import sneer.foundation.brickness.Brick;
import dfcsantos.wusic.Track;

@Brick
public interface TrackPlayer {

	TrackContract startPlaying(Track track, Runnable toCallWhenFinished);
}
/*
 
 daniel 0 /80 1/rock 1/queen 1/bomemian
 daniel 0 /80 1/rock 1/queen 1/love
 daniel 0 /samba -1/alcione -1/sei la o q
 fulano -1/girlfriend
 cicrano +1

File (Menu)
	Choose Song Folder  (Persist with BrickStateStore - ex: OwnNameKeeper   SongFolderKeeper)
	
 o  Play My Songs
 o  Play Songs from Peers (X)   (FolderConfig.tmpFolderFor(brick)/staging)
 
Song Playing (Label)

 [> / ||]    [>>]    [Me Too :)]    [No Way :(]
 
 
 Me Too: move from staging area to SongFolder/metoo.
 No Way: Delete from staging area.
 
 */