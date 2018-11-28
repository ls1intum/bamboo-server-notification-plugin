Artemis Notifications for Bamboo
==============================

This plugin sends [Bamboo](https://www.atlassian.com/software/bamboo) notifications to any server URL.

It allows any Bamboo notification to be posted to a specific server URL.

This plugin allows you to take advantage of Bamboo's:

-	flexible notification system (ie tell me when this build fails more than 5 times!)

Notifications Supported
-----------------------

-	all

Setup
-----

1.	Go to the *Notifications* tab of the *Configure Plan* screen.
2.	Choose a *Recipient Type* of *Server*
3.	Configure your *Server URL*
4.	You're done! Go and get building.

Compiling from source
---------------------

You first need to [Set up the Atlassian Plugin SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project). Or you could just do a `brew tap atlassian/tap; brew install atlassian/tap/atlassian-plugin-sdk` on a mac is you use HomeBrew... At the project top level (where the pom.xml is) :

1.	Compile : `atlas-mvn compile`
2.	Run : `atlas-run`
3.	Debug : `atlas-debug`

Feedback? Questions?
--------------------

Email: krusche@in.tum.de
