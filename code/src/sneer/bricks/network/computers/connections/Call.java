package sneer.bricks.network.computers.connections;

import sneer.bricks.identity.seals.Seal;


public interface Call {
	String callerName();
	Seal callerSeal();
}
