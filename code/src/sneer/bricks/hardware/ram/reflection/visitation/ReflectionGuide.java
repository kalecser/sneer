package sneer.bricks.hardware.ram.reflection.visitation;

import sneer.foundation.brickness.Brick;


@Brick
public interface ReflectionGuide {

	public void guide(ReflectionVisitor visitor, Object start);

}