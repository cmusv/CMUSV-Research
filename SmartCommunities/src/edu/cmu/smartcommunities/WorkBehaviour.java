package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.CyclicBehaviour;
import edu.cmu.smartcommunities.simulation.TimeAgent;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.ExtendedProperties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkBehaviour
   extends CyclicBehaviour
   {
   private        final DateFormat dateFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static final long       serialVersionUID = -2205195826093132722L;
   private        final AID        topic;

   public WorkBehaviour(final Agent agent,
                        final ExtendedProperties extendedProperties)
      {
      super(agent);

      AID topic = null;

      try
         {
         final TopicManagementHelper topicHelper = (TopicManagementHelper) agent.getHelper(TopicManagementHelper.SERVICE_NAME);

         topic = topicHelper.createTopic(TimeAgent.SERVICE_TYPE);
         topicHelper.register(topic);
         }
      catch (final ServiceException serviceException)
         {
         logger.error("Unable to create time simulation topic.",
                      serviceException);
         }
      finally
         {
         this.topic = topic;
         }
      }

   @Override
   public void action()
      {
      logger.trace("Begin action");

      final ACLMessage incomingMessage = myAgent.receive(MessageTemplate.MatchTopic(topic));

      if (incomingMessage == null)
         {
         block();
         }
      else
         {
         final String content = incomingMessage.getContent();

         logger.debug("It's now " + dateFormat.format(new Date(Long.parseLong(content.substring(content.indexOf(' ') + 1, content.indexOf(')'))))));
         }
      logger.trace("End   action");
      }
   }
