package sneer.bricks.skin.imgselector;

import java.awt.Image;

import basis.brickness.Brick;
import basis.lang.Consumer;


@Brick
public interface ImageSelector {

	void open(Consumer<Image> avatarSetter);

}
