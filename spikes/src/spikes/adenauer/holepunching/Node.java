package spikes.adenauer.holepunching;


public class Node {
	private EndPoint _internal;
	private EndPoint _external;
	
	
	public Node(EndPoint internal, EndPoint external) {
		this._internal = internal;
		this._external = external;
	}


	public EndPoint getInternal() {
		return _internal;
	}
	
	
	public EndPoint getExternal() {
		return _external;
	}
}
