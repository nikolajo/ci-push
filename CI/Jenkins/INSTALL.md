<b>INSTALL</b><br/>
You can use the push-receiver.hpi in the bin folder or you can compile it yourself from the source in the source folder.
<br/>
The simplest thing is to download the push-receiver.hpi file from the bin directory to a local folder on your machine. 
Then open Jenkins in a browser and go to 'Manage Jenkins' -> 'Manage Plugins' -> 'Advanced / Upload Plugin' and choose the downloaded hpi file.
( a restart of Jenkins may be necessary )
<br/>
Alternatively you can download the push-receiver folder in the source folder and package the project with Maven (mvn clean package)
<br/>
Eventually the plugin will be made available from the official Jenkins plugin site.
<br/>
<br/>
<b>CONFIGURE</b><br/>
In Jenkins go to 'Manage Jenkins' -> 'Configure System'. Find the 'Push Trigger Server' section ( if you cannot see this section and you have installed the plugin as described above, please try to restart Jenkins )<br/>
In the Server field enter the name ( IP or DNS ) of the machine where the Push Server is installed. See under Server in this github project for instructions on how to install the Push Server.<br/>
The Server Routing Filter field can be left empty.<br/>
<br/>
In the relevant Jenkins job ( under 'Configure' ) now change the 'Build Triggers' to 'Push Trigger'<br/>
You must set a 'Repository Branch Name' and optionally a 'Repository Path'.<br/>
<br/>
The plugin will trigger a build if the branch and path matches the event commited by the VCS hook/trigger.<br/>
The branch must be an exact match and the beginning of the event path must match that of 'Repository Path'.<br/>


