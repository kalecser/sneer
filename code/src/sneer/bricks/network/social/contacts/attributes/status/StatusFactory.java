package sneer.bricks.network.social.contacts.attributes.status;

public interface StatusFactory {

	public static enum Status { ONLINE, OFFLINE, AWAY, BUSY };

	public static final Status DEFAULT_STATUS = Status.OFFLINE; 

}
