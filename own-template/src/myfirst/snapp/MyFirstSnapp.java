package myfirst.snapp;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Brick
@Snapp //A Snapp is simply a brick that gets loaded at startup. It normally calls other bricks to do useful things, so it typically has no public methods.
public interface MyFirstSnapp {}
