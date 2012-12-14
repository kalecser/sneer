LEGAL STUFF
===============

See license.txt in this same folder for licensing terms.



UNINSTALL
=============

To uninstall just delete the Sneer folder and any shortcuts created by Java Webstart.



OVERVIEW
============

Sneer's goal is to dismiss all internet middlemen such as email providers, Google and Facebook; and provide users with "the freedom to share information and hardware resources with friends", as described in the Sovereign Computing manifesto: http://www.advogato.org/article/808.html

Users are able to create, share and assemble their own tiny components called BRICKS (as in Lego) to build Sovereign Applications (Snapps).

The platform is itself built from a very small non-brick FOUNDATION and many bricks. Some services provided by bricks include: Threading, Networking, Tuple Space, Test Support, Gui Support and System Prevalence.

Sovereign Applications (SNAPPS) distributed with the platform include Wusic (music sharing) and Chat. 



FOLDER STRUCTURE
====================

Sneer is completely contained in the "Sneer folder". These are some relevant subfolders.

	{user home}/sneer
	
		/code - Sneer platform and bricks (Eclipse project).
		
			/docs - All sorts of useful info.
				license.txt - Licensing information.
				README.txt - This file.
				
			/own - User's bricks (Eclipse project).
			
		/data - All your Sneer data.
		
		/tmp - Temporary files.
		
		/logs - Log files.



OWN CODE
============

Every time you Webstart Sneer, if a newer version of the platform is available, it will be downloaded and the sneer/code folder will be updated.

The "own" folder is preserved, though, so you can use it to keep your own bricks. 

Sneer will run using the following classpath:

	FIRST : sneer/code/own/bin
	SECOND: sneer/code/bin

That means you can use the "own" folder to OVERRIDE any Sneer platform code, by copying that code to the "own" folder and editing it.

The "own" folder comes with an example Snapp you can tinker.



IMPORTING ECLIPSE PROJECTS
==============================

The sneer/code and sneer/code/own folders are completely self-contained Eclipse projects that compile with zero errors and zero warnings in Eclipse 3.4.1.

To import them into Eclipse do:

   File > Import > General > Existing Projects into Workspace > Select Root Directory: sneer/code

Do the same for sneer/code/own.

IMPORTANT: DO NOT CLICK ON "Copy projects to Workspace", or else you will be working on a copy and not on the live code.

Don't forget to REFRESH the Sneer project in Eclipse every time it is updated by Webstart. Also, Eclipse will have trouble compiling code while Sneer is running because it won't be able to delete the class files that are in use. 



RUNNING SNEER
=================

You can run Sneer via the Webstart shortcuts or using the "Sneer" Eclipse launch configuration. 

JDK6 or newer - Use the latest stable version of it. Before reporting bugs or problems, please make sure you are not using another JDK. To build Sneer we use the java compiler, so it has to be JDK, not the JRE.

Main Class: main.Sneer - Run it and follow usage instructions. You can also run main.SneerDummy for testing with two Sneer instances running.

home_override - You can set this Java system property to make Sneer run in a different directory, so you can have several different Sneer installations running at the same time. Example: java -Dhome_override=some/other/directory main.Sneer



RUNNING JUNIT TESTS
=======================

Use the "SneerTests" Eclipse launch configuration.



ANT BUILD
=============

build.xml - If you don't use Eclipse, you can use the ANT build file to compile Sneer and run all tests.



SHARING YOUR CODE
=====================

P2P brick sharing is not ready. For now, to share your code just zip your "own" folder (see above) and send it to us on the mailing list (see below).

If you become a frequent contributor, we can give you commit rights to our GIT repository.

If you are familiar with GIT you can get Sneer code directly from our GIT repository: http://github.com/bihaiko/sneer 



RESOURCES
=============

sneer/code/docs folder - This folder has lots of useful information. 

http://groups.google.com/group/sneercoders - If you have any questions, let us know.


See you, The Sneer Team.  :)
