package org.jenkinsci.plugins.pushreceiver;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class is a thread that handles listening on an AMQP topic
 *
 * @author Nikolaj Ougaard
 */
public class RabbitMQConnector implements Runnable {

    private static RabbitMQConnector INSTANCE = null;
    private static final String EXCHANGE_NAME = "PushTriggerTopic";
    private static final Logger LOGGER = Logger.getLogger(RabbitMQConnector.class.getName());

    private Connection connection = null;
    private Channel channel = null;
    private QueueingConsumer consumer = null;
    private Set<PushTrigger> triggers = new HashSet<PushTrigger>();
    private boolean running = false;
    private Thread thread = null;

    private RabbitMQConnector(String server, String routing) throws InstantiationException
    {
        try
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(server);
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();

            if ( routing == null || "".equals(routing.trim()) )
            {
                routing = "#";
            }
            if ( !routing.endsWith("#") )
            {
                routing = routing+".#";
            }

            channel.queueBind(queueName, EXCHANGE_NAME, routing);

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);

            running = true;
            thread = new Thread(this);
            thread.start();
        }
        catch ( IOException e )
        {
            LOGGER.severe("Could not create Rabbit MQ Connection. Please verify server name in the global configuration. "+e);
            throw new InstantiationException();
        }
    }

    public static RabbitMQConnector getInstance(String server, String routing)
    {
        if ( INSTANCE == null && server != null )
        {
            try
            {
                INSTANCE = new RabbitMQConnector(server, routing);
            }
            catch ( InstantiationException e )
            {
                INSTANCE = null;
            }
        }
        return INSTANCE;
    }

    public void addTrigger(PushTrigger trigger)
    {
        if ( !triggers.contains(trigger) )
        {
            triggers.add(trigger);
        }
    }

    public void removeTrigger(PushTrigger trigger)
    {
        if ( triggers.contains(trigger) )
        {
            triggers.remove(trigger);
        }
    }

    public void run()
    {
        LOGGER.info("Push trigger thread is running...");
        while ( running )
        {
            try
            {
                String path = "";
                String branch = "";
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                AMQP.BasicProperties props = delivery.getProperties();
                Object pathObject = props.getHeaders().get("path");
                if ( pathObject != null )
                {
                    path = pathObject.toString();
                }
                Object branchObject = props.getHeaders().get("branch");
                if ( branchObject != null )
                {
                    branch = branchObject.toString();
                }
                String message = new String(delivery.getBody());
                String routingKey = delivery.getEnvelope().getRoutingKey();
                LOGGER.info("   Push Trigger Received: branch='"+branch+"', path='"+path+"', routing='"+routingKey+"', message='" + message + "'");

                if ( path!=null && branch!=null )
                {
                    for ( PushTrigger trigger : triggers )
                    {
                        if ( branch.equalsIgnoreCase(trigger.getBranch()) && path.startsWith(trigger.getPath()) )
                        {
                            trigger.build();
                        }
                    }
                }
            }
            catch ( InterruptedException e )
            {
                //Ignore
            }
        }
        LOGGER.info("Push trigger thread is stopped...");
    }

    public static void shutdown()
    {
        if ( INSTANCE != null && INSTANCE.running )
        {
            INSTANCE.running = false;
            INSTANCE.thread.interrupt();
            try
            {
                if ( INSTANCE.channel!=null) INSTANCE.channel.close();
            }
            catch ( IOException e ) {}
            try
            {
                if ( INSTANCE.connection!=null) INSTANCE.connection.close();
            }
            catch ( IOException e ) {}
            INSTANCE = null;
        }
    }

}
