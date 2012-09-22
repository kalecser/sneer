package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.snapps.web.Web;

public class WebImpl implements Web {

	WeakContract ref;
	
	{
		try {
			ref = my(HttpServer.class).start(getPort(), new HttpHandler() {@Override public String replyFor(String target) {
				String ownName = my(Attributes.class).myAttributeValue(OwnName.class).currentValue();
				return "<h1>It works!! from+"+ ownName + "</h1>";
			}});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private int getPort() {
		String property = System.getProperty("sneer.web.port", ""+Web.PORT);
		return Integer.valueOf(property) ;
	}

	@Override
	public void crash() {
		ref.dispose();
	}
	
}
