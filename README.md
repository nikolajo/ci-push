ci-push
=======

This project aims to annihilate CI polling

The idea is to create a loosely coupled system that allows different VCS's to emit commit events and different CI platforms to listen for these events.
Thereby achieving push from the VCS to the CI platform. The purpose of the Push Server is to decouple the VCS and CI systems and thereby avoiding point to point integrations.
Once a hook or trigger is written for a VCS it can be used with any and all CI systems ( that has a plugin ).

The future might bring a Push Server in the cloud, so it isn't necessary for everybody to install their own server ( which will be identical to each other )


3 tiers are present:

<b>VCS tier:</b>
- A hook or trigger in the VCS system that detects the commit event and pushes it to the Push Server

<b>Push Server tier:</b>
- An black box server tier that routes the incoming events to relevant listeners

<b>CI tier:</b>
- A CI plugin that is able to register as a listener on the server tier and when notified trigger a build

The glue that binds these tiers is the event. The event has two attributes: branch and path


USING CI-PUSH
==============
<b>ELIMINATE ALL POLLING IN 15 MINUTES</b> ( currently only if you use Git and Jenkins on Windows )
- To get started install the Server as described in the Server section.
- Then install and configure the Git hook as described in the VCS/Git/Windows folder.
- Then install and configure the Jenkins CI plugin as described in the CI/Jenkins folder. 

If you can't find your VCS or CI on your desired platform, please contribute and create the appropriate triggers/hooks and/or plugins, so others can benefit. 
See below in the DEVELOPING CI-PUSH section.



VCS:
	All the available hooks and triggers can be found in the VCS folder. They are subdivided by VCS system and platform.
A Git hook for windows can be found in the folder VCS/Git/Windows/. Each folder will also contain a INSTALL.md file that describes
how to install/configure/use the hook.

Server:
	The black box server can be found in the Server folder. It consists of a Mule ESB Community Edition and a Mule flow and a RabbitMQ ( AMQP ) installation.
Each of these components must be installed on a server that your VCS and CI systems can reach. See the INSTALL.md files in the Server/ESB and Server/AMQP
folders for detailed instructions.

CI:
	All plugins for the CI systems can be found in the CI folder. They are subdivede by CI system and platform ( if necessary )
A Jenkins plugin can be found in CI/Jenkins. Each folder contains a INSTALL.md file that describes how to install/configure/use the plugin.
	
	
	
DEVELOPING CI-PUSH
===================
Coming....