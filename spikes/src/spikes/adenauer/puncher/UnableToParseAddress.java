package spikes.adenauer.puncher;


class UnableToParseAddress extends Exception {

	public UnableToParseAddress(String message) {
		super(message);
	}

	public UnableToParseAddress(String message, Throwable t) {
		super(message, t);
	}

}
