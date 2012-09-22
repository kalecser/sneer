package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import basis.lang.Consumer;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.snapps.owninfo.OwnInfo;
import sneer.bricks.snapps.web.Web;

public class WebImpl implements Web {

	WeakContract ref;
	
	{
		
		
		my(TupleSpace.class).addSubscription(WebRequest.class, new Consumer<WebRequest>() {@Override public void consume(WebRequest value) {
				my(TupleSpace.class).add(new WebResponse("I'm here. I am "+my(OwnName.class).toString()));
		}});
		
		final AtomicReference<String> lastContents = new AtomicReference<String>();
		
		my(TupleSpace.class).addSubscription(WebResponse.class, new Consumer<WebResponse>() {@Override public void consume(WebResponse value) {
			lastContents.set(value._contents);
		}});
		
		final SealForUrl sealForUrl = new SealForUrl(new ContactProviderImpl());
		try {
			ref = my(HttpServer.class).start(getPort(), new HttpHandler() {@Override public String replyFor(String target) {
				Seal sealForUrlOrNull = sealForUrl.getSealForUrlOrNull(target);
				if(sealForUrlOrNull == null) return "Sorry! <br>Url not found.";
				Seal sealForUrl = sealForUrlOrNull;
				my(TupleSpace.class).add(new WebRequest(sealForUrl));
				String lastContentBroadcasted = lastContents.get();
				if(lastContentBroadcasted != null && !lastContentBroadcasted.isEmpty()){
					lastContents.set("");
					return lastContentBroadcasted;
				}
				return "<h1>Please refresh!! </h1><br>requestFor: "+sealForUrl;
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
