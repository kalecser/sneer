package dfcsantos.wusic.impl;

import dfcsantos.wusic.Track;


abstract class TrackSourceStrategy {

	abstract Track nextTrack();

	abstract void noWay();

}
