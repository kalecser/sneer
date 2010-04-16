package sneer.bricks.software.code.compilers.scalasample.impl

import sneer.foundation.environments.Environments.my

import sneer.bricks.software.code.compilers.scalasample._
import sneer.bricks.hardware.io.log._

class HelloScalaImpl extends HelloScala {

	my(classOf[Logger]).log("Hello from Scala")

}