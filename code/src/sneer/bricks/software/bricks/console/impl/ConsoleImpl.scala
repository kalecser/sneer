package sneer.bricks.software.bricks.console.impl

import sneer.bricks.software.bricks.console.Console
import sneer.bricks.skin.main.menu.MainMenu
import basis.environments.Environments._
import groovy.ui.{ Console => GroovyConsole }
import basis.environments.Environment
import groovy.lang.GroovyShell
import groovy.lang.GroovyCodeSource
import basis.lang.Closure

class ConsoleImpl extends Console {

	{
		my(classOf[MainMenu]).menu().addAction(60, "Groovy console", new Runnable {
			override def run = {
				val console = new GroovyConsole
				console.setShell(customShellWithEnvironment)
				console.run
			}
		})
	}

	private def customShellWithEnvironment = {
		val environment = my(classOf[Environment])
		val shell = new GroovyShell {
			override def run(scriptText: String, fileName: String, args: Array[String]) = {
				var res: Object = null
				def callSuper(scriptText: String, fileName: String, args: Array[String]) =
					res = super.run(scriptText, fileName, args)
				runWith(environment, new Closure { override def run =
					callSuper(scriptText, fileName, args)
				})
				res
			}
		}
		shell.setVariable("env", environment)
		shell
	}

}