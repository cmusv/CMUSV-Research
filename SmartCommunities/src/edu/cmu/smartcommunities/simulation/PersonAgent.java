package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
// import edu.cmu.smartcommunities.utilities.Parser;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonAgent
   extends Agent
   {
   private static final String                className                  = PersonAgent.class.getName();
// private              String                currentWorkSpaceAgentName  = null;
   private              DateFormat            dateTimeFormat             = new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat);
   private              String                defaultWorkSpaceAgentName  = null;
// public  static final String                entering                   = "(entering (time ";
// public  static final String                leaving                    = "(leaving (time ";
   private        final String[]              meetingRoomAgentName       = new String[24];
   private              double                percentMeetingTime         = 0;
   private              Date                  preferredWorkStartDateTime = null;
   private              Date                  preferredWorkStopDateTime  = null;
// public  static final String                physicalLocationOntology   = className + ":Physical Location"; 
   private static final long                  serialVersionUID           = 9023344232278656873L;
   private              double                timeAllocatedToMeetings    = 0;
   private              TopicManagementHelper topicManagementHelper      = null;
   private              Date                  workStartDateTime          = null;
   private              Date                  workStopDateTime           = null;

   private String getDateTimeProperty(final String propertyName)
      {
      return extendedProperties.getProperty(propertyName, "2012-01-01 00:00:00");
      }

   private Date parseDateTime(final String dateTimeString)
      throws ParseException
      {
      return dateTimeFormat.parse(dateTimeString);
      }

   private String getDefaultWorkSpaceAgentName(final int hour)
      {
      return hour < preferredWorkStartDateTime.getHours() || hour >= preferredWorkStopDateTime.getHours() ? null : defaultWorkSpaceAgentName;
      }

   protected void setup()
      {
      String dateTimeString = null;

      super.setup();
      try
         {
         defaultWorkSpaceAgentName = extendedProperties.getProperty("WorkSpaceName");
         percentMeetingTime = Double.parseDouble(extendedProperties.getProperty("PercentMeetingTime", "0.125"));
         dateTimeString = getDateTimeProperty("PreferredWorkStartTime");
         preferredWorkStartDateTime = parseDateTime(dateTimeString);
         dateTimeString = getDateTimeProperty("PreferredWorkStopTime");
         preferredWorkStopDateTime = parseDateTime(dateTimeString);
         topicManagementHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
         workStartDateTime = preferredWorkStartDateTime;
         workStopDateTime = preferredWorkStopDateTime;
         addBehaviour(new ManageMeetingsBehaviour(this));
         addBehaviour(new WorkBehaviour(this));
         }
      catch (final ParseException parseException)
         {
         logger.error("Unable to interpret date/time:  " + dateTimeString,
                      parseException);
         }
      catch (final ServiceException serviceException)
         {
         logger.error("Unable to obtain the topic management helper.",
                      serviceException);
         }
      }

   private class ManageMeetingsBehaviour
      extends CyclicBehaviour
      {
   // private        final AID        meetingCreationTopic;
      private static final long serialVersionUID = -6090924341976674951L;

      public ManageMeetingsBehaviour(final Agent agent)
         {
         super(agent);

         AID meetingCreationTopic = null;

         try
            {
            meetingCreationTopic = topicManagementHelper.createTopic(MeetingRoomAgent.serviceType);
            topicManagementHelper.register(meetingCreationTopic);
            }
         catch (final ServiceException serviceException)
            {
            logger.error("Unable to subscribe to the meeting creation topic.",
                         serviceException);
            }
         finally
            {
         // this.meetingCreationTopic = meetingCreationTopic;
            }
         }

      @Override
      public void action()
         {
         logger.trace("Begin ManageMeetingsBehaviour.action");

         final ACLMessage inboundMessage = receive(MessageTemplate.MatchOntology(MeetingRoomAgent.meetingOntology));

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
         // logger.debug("Receiving (" + inboundMessage.getPerformative() + "):  " + inboundMessage.getContent());
         // final String dateTimeString = new Parser(inboundMessage.getContent()).getRange(1, 2);

            try
               {
               final Date meetingStartDateTime = (Date) inboundMessage.getContentObject();
               final int  meetingStartHour     = meetingStartDateTime.getHours();

               switch (inboundMessage.getPerformative())
                  {
                  case ACLMessage.CONFIRM:
                     {
                     final Date meetingStopDateTime = (Date) meetingStartDateTime.clone();

                     meetingStopDateTime.setHours(meetingStopDateTime.getHours() + 1);
                     if (meetingStartDateTime.before(workStartDateTime))
                        {
                        workStartDateTime = meetingStartDateTime;
                        }
                     if (meetingStopDateTime.after(workStopDateTime))
                        {
                        workStopDateTime = meetingStopDateTime;
                        }
                     break;
                     }
                  case ACLMessage.DISCONFIRM:
                     {
                     meetingRoomAgentName[meetingStartHour] = getDefaultWorkSpaceAgentName(meetingStartHour);
                     timeAllocatedToMeetings -= 1;
                     break;
                     }
                  case ACLMessage.PROPOSE:
                     {
                     final ACLMessage outboundMessage               = inboundMessage.createReply();
                           int        performative                  = ACLMessage.REJECT_PROPOSAL;
                     final String     scheduledMeetingRoomAgentName = meetingRoomAgentName[meetingStartHour];

                     outboundMessage.setContentObject(meetingStartDateTime);
                     outboundMessage.setOntology(inboundMessage.getOntology());
                     if (scheduledMeetingRoomAgentName == null || scheduledMeetingRoomAgentName.equals(defaultWorkSpaceAgentName))
                        {
                        if (timeAllocatedToMeetings / 8.0 < percentMeetingTime)
                           {
                           if (Math.random() < percentMeetingTime)
                              {
                              meetingRoomAgentName[meetingStartHour] = inboundMessage.getSender().getLocalName();
                              performative = ACLMessage.ACCEPT_PROPOSAL;
                              timeAllocatedToMeetings += 1;
                              }
                           }
                        }
                     outboundMessage.setPerformative(performative);
                     send(outboundMessage);
                  // logger.debug("Sending (" + outboundMessage.getPerformative() + "):  " + outboundMessage.getContent());
                     break;
                     }
                  default:
                     {
                     logger.warn("Unexpected message performative:  " + ACLMessage.getPerformative(inboundMessage.getPerformative()));
                     }
                  }
               }
            catch (final IOException ioException)
               {
               logger.error("Unable to set simulated date time content object:  " + ioException.getMessage(),
                            ioException);
               }
         // catch (final ParseException parserException)
            catch (final UnreadableException unreadableException)
               {
            // logger.error("Unable to parse meeting start time:  " + dateTimeString,
            //              parserException);
               logger.error("Unable to get simulated time content object:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         logger.trace("End   ManageMeetingsBehaviour.action");
         }
      }

   private class WorkBehaviour
      extends CyclicBehaviour
      {
   // private        final DateFormat      dateFormat          = new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat);
      private        final MessageTemplate messageTemplate     = MessageTemplate.MatchOntology(TimeAgent.timeSimulationOntology);
      private static final long            serialVersionUID    = 1833830971141389258L;
      private              Date            simulatedDateTime   = null;
      private        final AID             timeSimulationTopic;

      public WorkBehaviour(final Agent agent)
         {
         super(agent);

         AID timeSimulationTopic = null;

         try
            {
            timeSimulationTopic = topicManagementHelper.createTopic(TimeAgent.serviceType);
            topicManagementHelper.register(timeSimulationTopic);
            }
         catch (final ServiceException serviceException)
            {
            logger.error("Unable to subscribe to the time simulation topic.",
                         serviceException);
            }
         finally
            {
            this.timeSimulationTopic = timeSimulationTopic;
            }
         }

      @Override
      public void action()
         {
         logger.trace("Begin WorkBehaviour.action");

         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
         // final String content           = inboundMessage.getContent();
         // final Date   simulatedDateTime = new Date(Long.parseLong(content.substring(content.indexOf(' ') + 1, content.indexOf(')'))));
            try
               {
               final Date simulatedDateTime = (Date) inboundMessage.getContentObject();

            // logger.debug("It's now " + dateFormat.format(simulatedDateTime));
               if (this.simulatedDateTime == null || (this.simulatedDateTime.getHours() > simulatedDateTime.getHours()))
                  {
               // currentWorkSpaceAgentName = null;
                  workStartDateTime = preferredWorkStartDateTime; // TODO:  Add some randomness
                  workStopDateTime = preferredWorkStopDateTime; // TODO:  Add some randomness
                  for (int hour = 0; hour < meetingRoomAgentName.length; hour++)
                     {
                     meetingRoomAgentName[hour] = getDefaultWorkSpaceAgentName(hour);
                  // if (hour == 0) logger.debug("At hour " + hour + ", scheduled for " + meetingRoomAgentName[hour]);
                     }
                  timeAllocatedToMeetings = 0;
                  }
               else
                  {
                  final int hour = simulatedDateTime.getHours();

                  if (this.simulatedDateTime.getHours() == 5 && hour == 6) // 06:00
                     {
                     synchronized (PersonAgent.class)
                        {
                        for (int i = 0; i < meetingRoomAgentName.length; i++)
                           {
                           logger.debug("At hour " + i + ", scheduled for " + meetingRoomAgentName[i]);
                           }
                        }
                     }
                  final int    previousHour            = (hour + meetingRoomAgentName.length - 1) % meetingRoomAgentName.length;
                  final String enteringMeetingRoomName = meetingRoomAgentName[hour];
                  final String leavingMeetingRoomName  = meetingRoomAgentName[previousHour];

                  logger.debug("Previous (" + previousHour + "):  " + meetingRoomAgentName[previousHour]);
                  logger.debug("Current  (" + hour         + "):  " + meetingRoomAgentName[hour]);
                  if (leavingMeetingRoomName != null /* && !leavingMeetingRoomName.equals(enteringMeetingRoomName) */)
                     {
                     final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     outboundMessage.addReceiver(new AID(leavingMeetingRoomName, AID.ISLOCALNAME));
                     outboundMessage.setContentObject(simulatedDateTime);
                     outboundMessage.setOntology(WorkSpaceAgent.leavingOntology);
                     send(outboundMessage);
                     logger.debug("Sending leaving message");
                     }
                  if (enteringMeetingRoomName != null /* && !enteringMeetingRoomName.equals(leavingMeetingRoomName) */)
                     {
                     final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     outboundMessage.addReceiver(new AID(enteringMeetingRoomName, AID.ISLOCALNAME));
                     outboundMessage.setContentObject(simulatedDateTime);
                     outboundMessage.setOntology(WorkSpaceAgent.enteringOntology);
                     send(outboundMessage);
                     logger.debug("Sending entering message");
                     }
                  }
               /*
               if (this.simulatedDateTime.before(workStartDateTime) && !simulatedDateTime.before(workStartDateTime))
                  {
                  logger.debug("Start work");
                  logger.debug("enteringMeetingRoomName:  " + enteringMeetingRoomName);
                  logger.debug("leavingMeetingRoomName:   " + leavingMeetingRoomName);
                  enteringAgentId = new AID();
               // enteringAgentId.setLocalName(enteringMeetingRoomName == null ? defaultWorkSpaceAgentName : enteringMeetingRoomName);
                  enteringAgentId.setLocalName(enteringMeetingRoomName);
                  leavingAgentId = null;
                  logger.debug("entering:  " + enteringAgentId.getLocalName());
                  }
               else
                  {
                  if (simulatedDateTime.after(workStopDateTime) && !simulatedDateTime.after(workStopDateTime))
                     {
                     logger.debug("Stop work");
                     logger.debug("enteringMeetingRoomName:  " + enteringMeetingRoomName);
                     logger.debug("leavingMeetingRoomName:   " + leavingMeetingRoomName);
                     enteringAgentId = null;
                     leavingAgentId = new AID();
                  // leavingAgentId.setLocalName(leavingMeetingRoomName == null ? defaultWorkSpaceAgentName : leavingMeetingRoomName);
                     leavingAgentId.setLocalName(leavingMeetingRoomName);
                     logger.debug("leaving:   " + leavingAgentId.getLocalName());
                     }
                  else
                     {
                     if (simulatedDateTime.before(workStartDateTime) || simulatedDateTime.after(workStopDateTime))
                        {
                        logger.debug("Not at work");
                        currentWorkSpaceAgentName = null;
                        enteringAgentId = null;
                        leavingAgentId = null;
                        }
                     else
                        {
                        logger.debug("At work");
                        logger.debug("enteringMeetingRoomName:  " + enteringMeetingRoomName);
                        logger.debug("leavingMeetingRoomName:   " + leavingMeetingRoomName);
                        enteringAgentId = new AID();
                     // enteringAgentId.setLocalName(enteringMeetingRoomName == null ? defaultWorkSpaceAgentName : enteringMeetingRoomName);
                        enteringAgentId.setLocalName(enteringMeetingRoomName);
                        leavingAgentId = new AID();
                     // leavingAgentId.setLocalName(leavingMeetingRoomName == null ? defaultWorkSpaceAgentName : leavingMeetingRoomName);
                        leavingAgentId.setLocalName(leavingMeetingRoomName);
                        logger.debug("leaving:   " + leavingAgentId.getLocalName());
                        logger.debug("entering:  " + enteringAgentId.getLocalName());
                        }
                     }
                  }
               if (leavingAgentId != null && !leavingMeetingRoomName.equals(enteringMeetingRoomName))
                  {
                  final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                  outboundMessage.addReceiver(leavingAgentId);
                  outboundMessage.setContent("(leaving)");
                  outboundMessage.setOntology(physicalLocationOntology);
                  send(outboundMessage);
                  }
               if (enteringAgentId != null && !enteringMeetingRoomName.equals(leavingMeetingRoomName))
                  {
                  final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                  outboundMessage.addReceiver(enteringAgentId);
                  outboundMessage.setContent("(entering)");
                  outboundMessage.setOntology(physicalLocationOntology);
                  send(outboundMessage);
                  }
               */
               // TODO:  lunch
               this.simulatedDateTime = simulatedDateTime;
               }
            catch (final IOException ioException)
               {
               logger.error("Unable to set simulated time content:  " + ioException.getMessage(),
                            ioException);
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get simulated time content:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         logger.trace("End   WorkBehaviour.action");
         }
      }
   }
