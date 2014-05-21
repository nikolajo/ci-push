<b>INSTALL</b>
You can use the push-receiver.hpi in the bin folder or you can compile it yourself from the source in the source folder.

The simplest thing is to download the push-receiver.hpi file from the bin directory to a local folder on your machine. 
Then open Jenkins in a browser and go to 'Manage Jenkins' -> 'Manage Plugins' -> 'Advanced / Upload Plugin' and choose the downloaded hpi file.
( a restart of Jenkins may be necessary )

Alternatively you can download the push-receiver folder in the source folder and package the project with Maven ( mvn clean package )


<b>CONFIGURE</b>
In Jenkins go to 'Manage Jenkins' -> 'Configure System'. Find the 'Push Trigger Server' section ( if you cannot see this section and you have installed the plugin as described above, please try to restart Jenkins )
In the Server field enter the name ( IP or DNS ) of the machine where the Push Server is installed. See under Server in this github project for instructions on how to install the push server.
The Server Routing Filter field can be left empty.

In the relevant Jenkins job ( under 'Configure' ) now change the 'Build Triggers' to 'Push Trigger'
You must set a 'Repository Branch Name' and optionally a 'Repository Path'.


