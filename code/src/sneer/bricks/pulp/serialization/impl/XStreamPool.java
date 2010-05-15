package sneer.bricks.pulp.serialization.impl;


import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

class XStreamPool {

	private static List<XStream> _workers = new LinkedList<XStream>();


	static XStream borrowWorker() {
		synchronized (_workers) {
			return _workers.isEmpty()
				? createXStream()
				: _workers.remove(0);
		}
	}


	static void returnWorker(XStream worker) {
		synchronized (_workers) {
			_workers.add(worker);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	static private XStream createXStream() {
		return new XStream(null, new ClassMapper(), new XppDriver());
	}

}
