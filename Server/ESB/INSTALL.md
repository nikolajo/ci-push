<b>INSTALL</b><br/>
RabbitMQ must be installed for the Mule ESB flow to run correctly. See Server/AMQP/INSTALL.md
Simply follow the instructions on http://www.mulesoft.org/download-mule-esb-community-edition
Download and unpack on a machine that is to be used as the Push Server. Run Mule either by hand or by installing it as a service.
<br/>
<b>CONFIGURE</b><br/>
Download and copy the push-trigger.zip in the push-trigger-flow/bin folder to your Push Server installation's app folder.
Alternatively use Mule Studio to assemble the push-trigger flow by downloading and opening the push trigger project from the push-trigger-flow/source folder.
<br/>
You must setup a public writable share in order for the Push Server file interface to function correctly.
The default share name that must be created is 'pushfiles'. It must be created on the Push Server machine and be a public writable share. 
If in doubt consult your operating system guide for how to setup a public share.
<br/>

