package sneer.bricks.network.computers.http.server;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import basis.brickness.Brick;

@Brick
public interface HttpServer {

	WeakContract start(int port, HttpHandler httpHandler) throws IOException;

}
