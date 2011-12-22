package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeAgent
   extends Agent // SubscribableServiceAgent
   {
// private static final String          ONTOLOGY                 = "edu.cmu.smartcommunities.simulation.TimeAgent";
   public  static final String          SERVICE_TYPE             = "TimeSimulation";
// public  static final String          TIME_SIMULATION_ONTOLOGY = ONTOLOGY + ":" + SERVICE_TYPE;
// public  static final MessageTemplate messageTemplate          = MessageTemplate.MatchOntology(TIME_SIMULATION_ONTOLOGY);
   private static final long            serialVersionUID         = 3094307379768714926L;
   private              long            simulatedTime            = 0;

   protected void setup()
      {
      String startTimeString = "2011-12-01 00:00:00";

   // serviceType = SERVICE_TYPE;
      super.setup();
   // addBehaviour(new PassTimeBehaviour(this, extendedProperties, subscriberMap));
      try
         {
         final long clockTickInterval        = extendedProperties.getIntProperty("ClockTickInterval", 1000);
         final long millisecondsPerClockTick = extendedProperties.getIntProperty("MillisecondsPerClockTick", 1000);
         final AID  topic                    = ((TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME)).createTopic(SERVICE_TYPE);

         startTimeString = extendedProperties.getProperty("StartTime", startTimeString);
         simulatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTimeString).getTime();
         addBehaviour(new TickerBehaviour(this, clockTickInterval)
            {
            private static final long serialVersionUID = -1515261800639962675L;

            public void onTick()
               {
               final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);

               aclMessage.addReceiver(topic);
               aclMessage.setContent("(time " + simulatedTime + ")");
               simulatedTime += millisecondsPerClockTick;
               myAgent.send(aclMessage);
               }
            });
         }
      catch (final ParseException parseException)
         {
         logger.error("Unable to parse start time of '" + startTimeString + "'.",
                      parseException);
         }
      catch (final ServiceException serviceException)
         {
         logger.error("Unable to create " + SERVICE_TYPE + " topic.",
                      serviceException);
         }
      }
   /*
   public static AID getTopic(final jade.core.Agent agent)
      throws ServiceException
      {
      return ((TopicManagementHelper) agent.getHelper(TopicManagementHelper.SERVICE_NAME)).createTopic(SERVICE_TYPE);
      }
   */
   }
