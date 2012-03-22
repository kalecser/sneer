package sneer.bricks.hardware.ram.reflection.visitation;

import basis.brickness.Brick;


@Brick
public interface ReflectionGuide {

	public void guide(ReflectionVisitor visitor, Object start);

}