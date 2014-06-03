ci-push
=======

This project aims to annihilate CI polling

The idea is to create a loosely coupled system that allows different VCS's to emit commit events and different CI platforms to listen for these events.
Thereby achieving push from the VCS to the CI platform. The purpose of the Push Server is to decouple the VCS and CI systems and thereby avoiding point to point integrations.
Once a hook or trigger is written for a VCS it can be used with any and all CI systems ( that has a plugin ) and vice versa.

The future might bring a Push Server in the cloud, so it isn't necessary for everybody to install their own server. However a cloud Push Server raisea all the usual questions regarding security and so on.

3 tiers are present:

<b>VCS tier:</b>
- A hook or trigger in the VCS system that detects the commit event and pushes it to the Push Server

<b>Push Server tier:</b>
- An black box server tier that routes the incoming events to relevant listeners

<b>CI tier:</b>
- A CI plugin that is able to register as a listener on the server tier and when notified trigger a build

USING ci-push
==============
<b>ELIMINATE ALL POLLING IN 15 MINUTES</b> ( currently only plugins for Jenkins and hooks for Git on Windows are available )
- To get started install the Server as described in the <a href="./Server">Server</a> section.
- Then install and configure the Git hook as described in the <a href="./VCS/Git/Windows">VCS/Git/Windows</a> folder.
- Then install and configure the Jenkins CI plugin as described in the <a href="./CI/Jenkins">CI/Jenkins</a> folder. 

If you can't find your VCS or CI on your desired platform, please contribute and create the appropriate triggers/hooks and/or plugins, so others can benefit. 
See below in the DEVELOPING ci-push section.

<b>VCS</b><br/>
All the available hooks and triggers can be found in the VCS folder. They are subdivided by VCS system and platform.
A Git hook for windows can be found in the folder VCS/Git/Windows/. Each folder will also contain an INSTALL.md file that describes
how to install/configure/use the hook.<br/>

<b>Server</b><br/>
The black box server can be found in the Server folder. It consists of a Mule ESB Community Edition and a Mule flow and a RabbitMQ ( AMQP ) installation.
Each of these components must be installed on a server that your VCS and CI systems can reach. See the INSTALL.md files in the Server/ESB and Server/AMQP
folders for detailed instructions.

<b>CI</b><br/>
All plugins for the CI systems can be found in the CI folder. They are subdivede by CI system and platform ( if necessary )
A Jenkins plugin can be found in CI/Jenkins. Each folder contains an INSTALL.md file that describes how to install/configure/use the plugin.
	
	
	
DEVELOPING ci-push
===================
Two types of plugins are required to use ci-push. One plugin ( called hook in this section ) is needed for the VCS system and another plugin is needed for the CI systen.<br/>
<br/>
<b>Developing a VCS hook</b><br/>
To develop a hook for the VCS system it is necessary to use and understand the VCS systems hook mechanism. For Git hooks are created as script files 
that are put in a specific place in the Git repository. For other VCS systems this may be different.<br/>
<br/>
The Push Server has 3 interfaces that a VCS hook can use:
- The file interface
- The http interface 
- The AMQP interface

You can choose which ever interface suits your purpose best and fits best with the given hooks in the VCS system.<br/><br/>
<b>The file interface</b><br/>
The file interface works by copying ( moving ) a file to a specific share on the Push Server. This share is typically setup when the Push Server is installed. The default share is
'pushfiles'. The file that is being moved to the share must contain two lines. A line that tells which branch the commited file was on and a line that tells which path the commited file has. An example of 
the contents of such a file is shown here:<br/>
<code>branch=master</code><br/>
<code>path=Java/banking/BankProject/com/example/banking/MyBank.java</code><br/>
The example here will then trigger all CI systems that are listening for events for branch master and paths matching part of Java/banking/BankProject/com/example/banking/MyBank.java<br/>
For reference look at the <a href="VCS/Git/Windows/post-commit-event.cmd">Git hook Windows script</a> - it simply creates and copies a file to the Push Server file share<br/>
<br/>
<b>The http interface</b><br/>
The http interface works by doing a http get on a specified URL. The URL is typically setup when the Push Server is installed. The default URL is http://&lt;IP/DNS of Push Server&gt;:8081/push?branch=&lt;branch&gt;&path=&lt;path&gt;,
where &lt;branch&gt; is the branch that the file was commited on and &lt;path&gt; is the path of the commited file.<br/>
<br/>
<b>The AMQP interface</b><br/>
The AMQP interface works by putting a message on the queue named <i>PushTriggerQueue</i> that the Push Server sets up when installed. The <i>PushTriggerQueue</i> can be found on the machine that the Push Server is installed on.<br/>
The message must contain the branch and path of the commited file as properties. See below Java example on how to do this. Other languages and examples are avialable at <a href="http://www.rabbitmq.com/getstarted.html">RabbitMQ Tutorials</a><br/>
<br/>
<code>			  ConnectionFactory factory = new ConnectionFactory();</code><br/>
<code>            factory.setHost("&lt;IP/DNS of Push Server&gt;");</code><br/>
<br/>
<code>            connection = factory.newConnection();</code><br/>
<code>            channel = connection.createChannel();</code><br/>
<br/>
<code>            channel.queueDeclare(QUEUE_NAME, false, false, false, null); //QUEUE_NAME is 'PushTriggerQueue'</code><br/>
<code>            String message = "Commit";</code><br/>
<br/>
<code>            Map&lt;String, Object&gt; props = new HashMap&lt;String, Object&gt;();</code><br/>
<code>            props.put("path","Java/banking/BankProject/com/example/banking/MyBank.java");</code><br/>
<code>            props.put("branch","master");</code><br/>
<br/>
<code>            AMQP.BasicProperties.Builder bob = new AMQP.BasicProperties.Builder();</code><br/>
<code>            AMQP.BasicProperties basicProps = bob.headers(props).build();</code><br/>
<code>            channel.basicPublish("", QUEUE_NAME, basicProps, message.getBytes());</code><br/>
<br/><br/><br/>
<b>Developing a CI plugin</b><br/>
The CI plugin is really about understanding the CI systems framework or way of working. All the CI system needs to do is to register as a listener on an AMQP topic named <i>PushTriggerTopic</i>.<br/>
This can be done in many ways and by different languages. See below for a Java example and look at <a href="http://www.rabbitmq.com/getstarted.html">RabbitMQ Tutorials</a> for examples in other languages.<br/>
<br/>
<code>            ConnectionFactory factory = new ConnectionFactory();</code><br/>
<code>            factory.setHost("&lt;IP/DNS of Push Server&gt;");</code><br/>
<code>            Connection connection = factory.newConnection();</code><br/>
<code>            Channel channel = connection.createChannel();</code><br/>
<br/>
<code>            channel.exchangeDeclare(EXCHANGE_NAME, "topic"); //EXCHANGE_NAME is 'PushTriggerTopic'</code><br/>
<code>            String queueName = channel.queueDeclare().getQueue();</code><br/>
<br/>
<code>            channel.queueBind(queueName, EXCHANGE_NAME, "#");</code><br/>
<br/>
<code>            QueueingConsumer consumer = new QueueingConsumer(channel);</code><br/>
<code>            channel.basicConsume(queueName, true, consumer);</code><br/>
<br/>
<code>            while (true) { //This might not be a reasonable loop</code><br/>
<code>					QueueingConsumer.Delivery delivery = consumer.nextDelivery();</code><br/>
<code> 					AMQP.BasicProperties props = delivery.getProperties();</code><br/>
<code>					String branch = props.getHeaders().get("branch").toString();</code><br/>
<code>					String path = props.getHeaders().get("path").toString();</code><br/>
<code>            }</code><br/>
<br/>
The <code>branch</code> and <code>path</code> now holds the values that the VCS system pushed and internal listeners should be notified. For reference look at the 
<a href="CI/Jenkins/source/push-receiver/src/main/java/org/jenkinsci/plugins/pushreceiver/RabbitMQConnector.java">Jenkins plugin</a> - especially the <code>run</code> method<br/>














