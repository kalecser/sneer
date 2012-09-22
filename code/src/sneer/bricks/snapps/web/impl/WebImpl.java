package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.snapps.owninfo.OwnInfo;
import sneer.bricks.snapps.web.Web;
import basis.lang.Consumer;

public class WebImpl implements Web {

	WeakContract ref, ref1, ref2;
	
	{
		ref1 = my(RemoteTuples.class).addSubscription(WebRequest.class, new Consumer<WebRequest>() {@Override public void consume(WebRequest value) {
			String response = "I'm here. I am "+my (Attributes.class).myAttributeValue(OwnName.class).currentValue();
			value.respond(response);
		}});
		
		final AtomicReference<String> lastContents = new AtomicReference<String>();
		
		ref2 = my(TupleSpace.class).addSubscription(WebResponse.class, new Consumer<WebResponse>() {@Override public void consume(WebResponse value) {
			lastContents.set(value._contents);
		}});
		
		final SealForUrl sealForUrl = new SealForUrl(new ContactProviderImpl());
		try {
			ref = my(HttpServer.class).start(getPort(), new HttpHandler() {@Override public String replyFor(String target) {
				Seal sealForUrlOrNull = sealForUrl.getSealForUrlOrNull(target);
				if(sealForUrlOrNull == null) return "Sorry! <br>Url not found.";
				Seal sealForUrl = sealForUrlOrNull;
				my(TupleSpace.class).add(new WebRequest(sealForUrl));
				String lastContentReceived = lastContents.get();
				lastContentReceived = (lastContentReceived == null)?"":lastContentReceived;
				if(!lastContentReceived.isEmpty()){
					lastContents.set("");
					return lastContentReceived;
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
