package sneer.bricks.network.computers.sockets.connections.impl;

import sneer.bricks.network.social.Contact;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class ConnectionsByContact {

	static private final CacheMap<Contact, ByteConnectionImpl> _cache = CacheMap.newInstance();


	static ByteConnectionImpl get(Contact contact) {
		return _cache.get(contact, new Producer<ByteConnectionImpl>() { @Override public ByteConnectionImpl produce() {
			return new ByteConnectionImpl();
		}});
	}


	static ByteConnectionImpl remove(Contact contact) {
		return _cache.remove(contact);
	}


	static Iterable<ByteConnectionImpl> all() {
		return _cache.values();
	}

}
