package org.pushtrigger.mule.agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleException;
import org.mule.api.agent.Agent;
import org.mule.api.lifecycle.InitialisationException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Copyright 2014 Nikolaj Ougaard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * This agent class runs at startup and creates the queue required by the push server amqp interface
 * 
 * @author Nikolaj Ougaard
 *
 */
public class CreateQueueAgent implements Agent {

    private static final String QUEUE_NAME = "PushTriggerQueue";

    @Override
	public void initialise() throws InitialisationException {
		//Ignore
	}

	@Override
	public void start() throws MuleException {
		System.out.println("Create Queue Agent is running...");

	    Connection connection = null;
        Channel channel = null;
        try
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World! I am creating a queue...";

            Map<String, Object> props = new HashMap<String, Object>();
            props.put("path","queue");
            props.put("branch","create");

            AMQP.BasicProperties.Builder bob = new AMQP.BasicProperties.Builder();
            AMQP.BasicProperties basicProps = bob.headers(props).build();

            channel.basicPublish("", QUEUE_NAME, basicProps, message.getBytes());
            System.out.println("Agent has created the queue...");
        }
        catch ( IOException e )
        {
            System.out.println("Something wrong "+e);
        }
        finally
        {
            try
            {
                if ( channel!=null) channel.close();
            }
            catch ( IOException e ) {}
            try
            {
                if ( connection!=null) connection.close();
            }
            catch ( IOException e ) {}
        }
	}

	@Override
	public void stop() throws MuleException {
		//Ignore	
	}

	@Override
	public void dispose() {
		//Ignore		
	}

	@Override
	public void setName(String name) {
		//Ignore	
	}

	@Override
	public String getName() {
		return "CreateQueueAgent";
	}

	@Override
	public String getDescription() {
		return "CreateQueueAgent creates the queue required by the push trigger.";
	}

}
