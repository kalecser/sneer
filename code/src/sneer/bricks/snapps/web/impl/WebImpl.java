package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.snapps.web.Web;

public class WebImpl implements Web {

	WeakContract ref;
	
	{
		final SealForUrl sealForUrl = new SealForUrl(new ContactProviderImpl());
		try {
			ref = my(HttpServer.class).start(getPort(), new HttpHandler() {@Override public String replyFor(String target) {
				Seal sealForUrlOrNull = sealForUrl.getSealForUrlOrNull(target);
				
				return "<h1>It works!! </h1><br>requestFor: "+sealForUrlOrNull;
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
