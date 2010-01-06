package sneer.bricks.pulp.dyndns.client;



//@Brick
//Fix: Running on more than one machine will cause redundant (abusive) updates.
// Stop persisting last discovered IP. Do a DNS lookup every time instead.

public interface DynDnsClient {}
