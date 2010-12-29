package sneer.bricks.software.bricks.snapploader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Snapp is short for SovereigN APP. It annotates a Brick that will be loaded at startup. */
@Retention(RetentionPolicy.RUNTIME)
public @interface Snapp {}
