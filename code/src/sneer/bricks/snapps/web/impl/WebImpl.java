package sneer.bricks.snapps.web.impl;

import static basis.environments.Environments.my;

import java.io.IOException;

import org.apache.commons.lang.UnhandledException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.http.server.HttpHandler;
import sneer.bricks.network.computers.http.server.HttpServer;
import sneer.bricks.snapps.web.Web;

public class WebImpl implements Web {

	WeakContract ref;
	
	{
		try {
			ref = my(HttpServer.class).start(27135, new HttpHandler() {@Override public String replyFor(String target) {
					return "<h1>It works!!</h1>";
			}});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
