package org.pushtrigger.mule.transformer;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;

import org.mule.transformer.AbstractMessageTransformer;

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

 * This class takes an incoming file and extracts the path and branch properties and puts them on the message as properties
 * 
 * @author Nikolaj Ougaard
 *
 */
public class FileTransformer extends AbstractMessageTransformer
{
    public Object transformMessage(MuleMessage message, String enc) throws TransformerException
    {
    	String payload = message.getPayload(String.class);
    	String[] lines = payload.split("\n");
    	String path = null;
    	String branch = null;
    	for ( String line : lines )
    	{
        	int p = line.indexOf("path=");
        	if ( p > -1 )
        	{
        		path = line.substring(p+5).trim();
        	}
        	else
        	{
        		int b = line.indexOf("branch=");
        		if ( b > - 1)
        		{
        			branch = line.substring(b+7).trim();
        		}
        	}
    	}
    	Map<String, Object> props = new HashMap<String, Object>();
    	props.put("branch",branch); 
    	props.put("path",path); 
    	props.put("routing-key",branch+"."+path); 
    	message.addProperties(props, PropertyScope.OUTBOUND);
    	return "CommitEvent";
    }
}
