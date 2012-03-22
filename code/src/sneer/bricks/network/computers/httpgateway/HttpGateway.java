package sneer.bricks.network.computers.httpgateway;

import java.io.IOException;

import basis.brickness.Brick;
import basis.lang.Consumer;


@Brick
public interface HttpGateway {

	void get(String httpUrl, Consumer<byte[]> client);
	void get(String httpUrl, Consumer<byte[]> client, Consumer<IOException> exceptionHandler);
	
}
