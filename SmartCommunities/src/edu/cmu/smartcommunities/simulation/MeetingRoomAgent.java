package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
//import edu.cmu.smartcommunities.utilities.Parser;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.Date;

public class MeetingRoomAgent
   extends WorkSpaceAgent
   {
   public  static final String className          = MeetingRoomAgent.class.getName();
   public  static final String meetingOntology    = className + ":Meeting";
   private              double percentUtilization = 0;
   private static final long   serialVersionUID   = -2497807731129780835L;
   public  static final String serviceType        = "Meeting Creation";

   protected void setup()
      {
      String percentUtilizationString = null;

      super.setup();
      try
         {
         percentUtilizationString = extendedProperties.getProperty("PercentUtilization", "0.5");
         percentUtilization = Double.parseDouble(percentUtilizationString);
         addBehaviour(new ManageMeetingsBehaviour(this));
         }
      catch (final NumberFormatException numberFormatException)
         {
         logger.error("Unable to parse PercentUtilization:  " + percentUtilizationString,
                      numberFormatException);
         }
      }

   private class ManageMeetingsBehaviour
      extends CyclicBehaviour
      {
   // private        final DateFormat      dateTimeFormat        = new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat);
      private              int[]           inviteeCount          = new int[24];
      private        final AID             meetingCreationTopic;
      private        final MessageTemplate messageTemplate       = MessageTemplate.or(MessageTemplate.MatchOntology(MeetingRoomAgent.meetingOntology),
                                                                                      MessageTemplate.MatchOntology(TimeAgent.timeSimulationOntology));
      private static final long            serialVersionUID      = 4270900829802841544L;
      private              Date            simulatedDateTime     = null;
   // private        final AID             timeSimulationTopic;

      public ManageMeetingsBehaviour(final Agent agent)
         {
         super(agent);

         logger.trace("Begin ManageMeetingsBehaviour.<init>");

         AID meetingCreationTopic = null;
      // AID timeSimulationTopic  = null;

         try
            {
            final TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);

            meetingCreationTopic = topicHelper.createTopic(serviceType);
            try
               {
            // timeSimulationTopic = topicHelper.createTopic(TimeAgent.serviceType);
            // topicHelper.register(timeSimulationTopic);
               topicHelper.register(topicHelper.createTopic(TimeAgent.serviceType));
               }
            catch (final ServiceException serviceException)
               {
               logger.error("Unable to subscribe to the time simulation topic.",
                            serviceException);
               }
            }
         catch (final ServiceException serviceException)
            {
            logger.error("Unable to obtain the topic management helper.",
                         serviceException);
            }
         finally
            {
            this.meetingCreationTopic = meetingCreationTopic;
         // this.timeSimulationTopic = timeSimulationTopic;
            }
         logger.trace("End   ManageMeetingsBehaviour.<init>");
         }

      @Override
      public void action()
         {
         logger.trace("Begin ManageMeetingsBehaviour.action");

         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
         // logger.debug("Receiving (" + inboundMessage.getPerformative() + "):  " + inboundMessage.getContent());
            final String ontology = inboundMessage.getOntology();

            if (meetingOntology.equals(ontology))
               {
               switch (inboundMessage.getPerformative())
                  {
                  case ACLMessage.ACCEPT_PROPOSAL:
                     {
                  // final String dateTimeString = new Parser(inboundMessage.getContent()).getRange(1, 2);
                  // meetingStartDateTime.setTime(Long.parseLong(new Parser(inboundMessage.getContent()).getToken(2)));
                     try
                        {
                        final Date meetingStartDateTime = (Date) inboundMessage.getContentObject();

                     // final int        meetingStartHour = dateTimeFormat.parse(dateTimeString).getHours();
                        final int        meetingStartHour = meetingStartDateTime.getHours();
                        final ACLMessage outboundMessage  = inboundMessage.createReply();
                        final int        performative     = inviteeCount[meetingStartHour] < capacity ? ACLMessage.CONFIRM : ACLMessage.DISCONFIRM;

                     // outboundMessage.setContent(inboundMessage.getContent());
                        outboundMessage.setContentObject(meetingStartDateTime);
                        outboundMessage.setOntology(meetingOntology);
                        outboundMessage.setPerformative(performative);
                        if (performative == ACLMessage.CONFIRM)
                           {
                           inviteeCount[meetingStartHour]++;
                           }
                        send(outboundMessage);
                        }
                  // catch (final ParseException parserException)
                  //    {
                  //    logger.error("Unable to parse meeting start time:  " + dateTimeString,
                  //                 parserException);
                  //    }
                     catch (final IOException ioException)
                        {
                        logger.error("Unable to set simulated date time content object:  " + ioException.getMessage(),
                                     ioException);
                        }
                     catch (final UnreadableException unreadableException)
                        {
                        logger.error("Unable to get simulated date time content object:  " + unreadableException.getMessage(),
                                     unreadableException);
                        }
                     break;
                     }
                  case ACLMessage.REJECT_PROPOSAL:
                     {
                     break;
                     }
                  default:
                     {
                     logger.warn("Unexpected performative received:  " + ACLMessage.getPerformative(inboundMessage.getPerformative()));
                     }
                  }
               }
            else
               {
               if (TimeAgent.timeSimulationOntology.equals(ontology))
                  {
               // final String content           = inboundMessage.getContent();
               // final Date   simulatedDateTime = new Date(Long.parseLong(content.substring(content.indexOf(' ') + 1, content.indexOf(')'))));
                  try
                     {
                     final Date simulatedDateTime = (Date) inboundMessage.getContentObject();

                     // logger.debug("It's now " + dateTimeFormat.format(simulatedDateTime));
                     if (this.simulatedDateTime == null || (this.simulatedDateTime.getHours() > simulatedDateTime.getHours()))
                        {
                        final int  firstMeetingStartHour = 8;
                        final int  lastMeetingStartHour  = 17;
                        final Date meetingStartDateTime  = (Date) simulatedDateTime.clone();

                        for (int hour = firstMeetingStartHour; hour <= lastMeetingStartHour; hour++)
                           {
                           if (hour != 12 && Math.random() < percentUtilization)
                              {
                              final ACLMessage outboundMessage = new ACLMessage(ACLMessage.PROPOSE);

                              meetingStartDateTime.setHours(hour);
                              outboundMessage.addReceiver(meetingCreationTopic);
                           // outboundMessage.setContent("(meeting " + dateTimeFormat.format(meetingStartDateTime) + ")");
                           // outboundMessage.setContent("(meeting (time " + simulatedDateTime.getTime() + "))");
                              outboundMessage.setContentObject(meetingStartDateTime);
                              outboundMessage.setOntology(meetingOntology);
                           // logger.debug("Sending (" + outboundMessage.getPerformative() + "):  " + outboundMessage.getContent());
                              send(outboundMessage);
                              }
                           }
                        }
                     this.simulatedDateTime = simulatedDateTime;
                     }
                  catch (final IOException ioException)
                     {
                     logger.error("Unable to set simulated date time content object:  " + ioException.getMessage(),
                                  ioException);
                     }
                  catch (final UnreadableException unreadableException)
                     {
                     logger.error("Unable to get simulated date time content object:  " + unreadableException.getMessage(),
                                  unreadableException);
                     }
                  }
               else
                  {
                  logger.warn("Unexpected ontology:  " + ontology);
                  }
               }
            }
         logger.trace("End   ManageMeetingsBehaviour.action");
         }
      }
   }
