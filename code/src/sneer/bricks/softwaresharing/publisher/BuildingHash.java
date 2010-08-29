package sneer.bricks.softwaresharing.publisher;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

/** Conveys the hash of the entire source folder (Building) of a given publisher at a certain point in time. */
public class BuildingHash extends Tuple {

	public final Hash value;

	public BuildingHash(Hash buildingHash_) {
		value = buildingHash_;
	}

}
