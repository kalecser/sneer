package spikes.sneer.bricks.pulp.own.avatar;

import java.awt.Image;

import basis.brickness.Brick;
import basis.lang.Consumer;

import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface OwnAvatarKeeper {

	Signal<Image> avatar(int squareSize);

	Consumer<Image> avatarSetter();
}
