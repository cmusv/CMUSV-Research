package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MeetingRoomAgent
   extends WorkSpaceAgent
   {
   private              int    capacity           = 0;
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
         capacity = extendedProperties.getIntProperty("Capacity", 0);
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
      private              Calendar        dateTime              = null;
      private              int[]           inviteeCount          = new int[24];
      private        final AID             meetingCreationTopic;
      private        final MessageTemplate messageTemplate       = MessageTemplate.or(MessageTemplate.MatchOntology(MeetingRoomAgent.meetingOntology),
                                                                                      MessageTemplate.MatchOntology(TimeAgent.timeSimulationOntology));
      private static final long            serialVersionUID      = 4270900829802841544L;

      public ManageMeetingsBehaviour(final Agent agent)
         {
         super(agent);

         logger.trace("Begin ManageMeetingsBehaviour.<init>");

         AID meetingCreationTopic = null;

         try
            {
            final TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);

            meetingCreationTopic = topicHelper.createTopic(serviceType);
            try
               {
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
            final String ontology = inboundMessage.getOntology();

            messagesReceived++;
            if (meetingOntology.equals(ontology))
               {
               switch (inboundMessage.getPerformative())
                  {
                  case ACLMessage.ACCEPT_PROPOSAL:
                     {
                     try
                        {
                        final Calendar   meetingStartDateTime = (Calendar) inboundMessage.getContentObject();
                        final int        meetingStartHour     = meetingStartDateTime.get(Calendar.HOUR_OF_DAY);
                        final ACLMessage outboundMessage      = inboundMessage.createReply();
                        final int        performative         = inviteeCount[meetingStartHour] < capacity ? ACLMessage.CONFIRM : ACLMessage.DISCONFIRM;

                        outboundMessage.setContentObject(meetingStartDateTime);
                        outboundMessage.setOntology(meetingOntology);
                        outboundMessage.setPerformative(performative);
                        if (performative == ACLMessage.CONFIRM)
                           {
                           inviteeCount[meetingStartHour]++;
                           }
                        send(outboundMessage);
                        messagesSent++;
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
                  try
                     {
                     final Calendar dateTime = (Calendar) inboundMessage.getContentObject();

                     logger.debug("Received time:  " + new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat).format(dateTime.getTime()));
                     if (this.dateTime == null || (this.dateTime.get(Calendar.HOUR_OF_DAY) > dateTime.get(Calendar.HOUR_OF_DAY)))
                        {
                        final int      firstMeetingStartHour = 8;
                        final int      lastMeetingStartHour  = 17;
                        final Calendar meetingStartDateTime  = (Calendar) dateTime.clone();

                        switch (dateTime.get(Calendar.DAY_OF_WEEK))
                           {
                           case Calendar.SATURDAY:
                              {
                              break;
                              }
                           case Calendar.SUNDAY:
                              {
                              break;
                              }
                           default:
                              {
                              for (int hour = firstMeetingStartHour; hour <= lastMeetingStartHour; hour++)
                                 {
                                 if (hour != 12 && Math.random() < percentUtilization)
                                    {
                                    final ACLMessage outboundMessage = new ACLMessage(ACLMessage.PROPOSE);

                                    meetingStartDateTime.set(Calendar.HOUR_OF_DAY, hour);
                                    meetingStartDateTime.set(Calendar.MINUTE, 0);
                                    outboundMessage.addReceiver(meetingCreationTopic);
                                    outboundMessage.setContentObject(meetingStartDateTime);
                                    outboundMessage.setOntology(meetingOntology);
                                    send(outboundMessage);
                                    messagesSent++;
                                    }
                                 }
                              }
                           }
                        }
                     this.dateTime = dateTime;
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
