package org.jenkinsci.plugins.pushreceiver;

import hudson.Extension;
import hudson.model.*;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This trigger waits for notifications.
 *
 * @author Nikolaj Ougaard
 */
public class PushTrigger extends Trigger<AbstractProject<?, ?>> {

    private static final Logger LOGGER = Logger.getLogger(PushTrigger.class.getName());

    private final String branch;
    private final String path;

    private AbstractProject project = null;

    @DataBoundConstructor
    public PushTrigger(String branch, String path)
    {
        super();
        this.branch = branch;
        this.path = path;
    }

    @Override
    public void start(AbstractProject<?, ?> project, boolean newInstance)
    {
        super.start(project, newInstance);
        this.project = project;

        if ( getDescriptor().getServer() != null && !"".equals(getDescriptor().getServer().trim()) )
        {
            RabbitMQConnector connector = RabbitMQConnector.getInstance(getDescriptor().getServer(), getDescriptor().getRouting());
            if ( connector != null )
            {
                connector.addTrigger(this);
            }
        }
        else
        {
            LOGGER.severe("Could not initialize push trigger because server name is not set in the global configuration.");
        }
    }

    @Override
    public void stop()
    {
        super.stop();

        RabbitMQConnector connector = RabbitMQConnector.getInstance(null, null);
        if ( connector != null )
        {
            connector.removeTrigger(this);
        }
    }

    public void build()
    {
        if ( project.isInQueue() )
        {
            project.scheduleBuild(5, null);
        }
        else
        {
            project.scheduleBuild(5, new Cause()
            {
                @Override
                public String getShortDescription()
                {
                    return "Build triggered by push event...";
                }
            });
        }
    }

    public String getBranch()
    {
        return branch;
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public DescriptorImpl getDescriptor()
    {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends TriggerDescriptor {

        public String server;
        public String routing;

        public DescriptorImpl()
        {
            load();
        }

        public FormValidation doCheckServer(@QueryParameter String value) throws IOException, ServletException
        {
            if (value.length() == 0) return FormValidation.error("Please set a server name!");
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Item item)
        {
            return true;
        }

        @Override
        public String getDisplayName()
        {
            return "Push Trigger";
        }

        public String getServer()
        {
            return server;
        }

        public String getRouting()
        {
            return routing;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException
        {
            server = formData.getString("server");
            routing = formData.getString("routing");
            save();
            return super.configure(req,formData);

        }
    }
}

