package sneer.bricks.pulp.dyndns.ownip;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface OwnIpDiscoverer {
	
	Signal<String> ownIp();

}
