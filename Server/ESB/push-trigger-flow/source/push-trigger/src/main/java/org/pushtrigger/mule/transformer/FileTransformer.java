package org.pushtrigger.mule.transformer;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;

import org.mule.transformer.AbstractMessageTransformer;

/**
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
