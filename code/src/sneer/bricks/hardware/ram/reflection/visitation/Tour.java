package sneer.bricks.hardware.ram.reflection.visitation;


public interface Tour {

	Tour tillHere();
	String direction();
	Object target();

}