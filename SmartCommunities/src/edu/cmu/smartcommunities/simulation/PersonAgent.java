package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PersonAgent
   extends Agent
   {
   private              Date                  agendaModificationDateTime = null;
   private              Date                  agendaReportDateTime       = null;
   private static final String                className                  = PersonAgent.class.getName();
   public  static final String                agendaOntology             = className + ":Agenda";
   private              DateFormat            dateTimeFormat             = new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat);
   private              String                defaultWorkSpaceAgentName  = null;
// private        final Map<Date, Meeting>    meetingMap                 = new TreeMap<>();
   private        final String[]              meetingRoomAgentName       = new String[24 * 60];
   private              String                movementModelFile          = null; // TODO:  get this from a property file.  don't use faculty
   public  static final String                movementOntology           = className + ":Movement";
   private              double                percentMeetingTime         = 0;
   private              Date                  preferredWorkStartDateTime = null;
   private              Date                  preferredWorkStopDateTime  = null;
   private static final long                  serialVersionUID           = 9023344232278656873L;
   private              double                timeAllocatedToMeetings    = 0;
   private              TopicManagementHelper topicManagementHelper      = null;
   private              Date                  workStartDateTime          = null;
   private              Date                  workStopDateTime           = null;

   private String getDateTimeProperty(final String propertyName)
      {
      return extendedProperties.getProperty(propertyName, "2012-01-03 00:00:00");
      }

   private String getDefaultWorkSpaceAgentName(final int hour)
      {
      return hour < preferredWorkStartDateTime.getHours() || hour >= preferredWorkStopDateTime.getHours() ? null : defaultWorkSpaceAgentName;
      }

   private Date parseDateTime(final String dateTimeString)
      throws ParseException
      {
      return dateTimeFormat.parse(dateTimeString);
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
      // addBehaviour(new MoveBehaviour(this));
         addBehaviour(new ReportAgendaBehaviour(this, 100, 3000));
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
      private              Date            dateTime         = null;
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(MeetingRoomAgent.meetingOntology);
      private static final long            serialVersionUID = -6090924341976674951L;

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
            try
               {
               final Date meetingStartDateTime = (Date) inboundMessage.getContentObject();
               final int  meetingStartHour     = meetingStartDateTime.getHours();
               final int  meetingLength        = 1; // TODO:  Make more flexible

               if (this.dateTime == null || (this.dateTime.getHours() > dateTime.getHours()))
                  {
                  workStartDateTime = preferredWorkStartDateTime; // TODO:  Add some randomness
                  workStopDateTime = preferredWorkStopDateTime; // TODO:  Add some randomness
                  for (int minute = 0; minute < meetingRoomAgentName.length; minute++)
                     {
                     meetingRoomAgentName[minute] = getDefaultWorkSpaceAgentName(minute / 60);
                     }
                  timeAllocatedToMeetings = 0;
                  this.dateTime = meetingStartDateTime;
                  }
               switch (inboundMessage.getPerformative())
                  {
                  case ACLMessage.CONFIRM:
                     {
                     final Date meetingStopDateTime = (Date) meetingStartDateTime.clone();
                  // final Date meetingStopDateTime = meetingMap.get(meetingStartDateTime).stopDateTime;

                     meetingStopDateTime.setHours(meetingStopDateTime.getHours() + meetingLength);
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
                  // meetingRoomAgentName[meetingStartHour] = getDefaultWorkSpaceAgentName(meetingStartHour);
                     for (int minute = 0; minute < 60; minute++)
                        {
                        meetingRoomAgentName[meetingStartHour * 60 + minute] = getDefaultWorkSpaceAgentName(meetingStartHour);
                        }
                  // meetingMap.remove(meetingStartDateTime);
                     timeAllocatedToMeetings -= meetingLength;
                     break;
                     }
                  case ACLMessage.PROPOSE:
                     {
                     final ACLMessage outboundMessage               = inboundMessage.createReply();
                           int        performative                  = ACLMessage.REJECT_PROPOSAL;
                     final String     scheduledMeetingRoomAgentName = meetingRoomAgentName[meetingStartHour * 60];

                     outboundMessage.setContentObject(meetingStartDateTime);
                     outboundMessage.setOntology(inboundMessage.getOntology());
                     if (scheduledMeetingRoomAgentName == null || scheduledMeetingRoomAgentName.equals(defaultWorkSpaceAgentName))
                        {
                        if (timeAllocatedToMeetings / 8.0 < percentMeetingTime)
                           {
                           if (Math.random() < percentMeetingTime)
                              {
                           // final Calendar calendar = new GregorianCalendar();

                           // calendar.setTime(meetingStartDateTime);
                           // calendar.add(Calendar.HOUR, meetingLength);
                           // meetingMap.put(meetingStartDateTime,
                           //                new Meeting(inboundMessage.getSender().getLocalName(),
                           //                            meetingStartDateTime,
                           //                            calendar.getTime()));
                              for (int minute = 0; minute < 60; minute++)
                                 {
                                 meetingRoomAgentName[meetingStartHour * 60 + minute] = inboundMessage.getSender().getLocalName(); // TODO:  Remove
                                 }
                              performative = ACLMessage.ACCEPT_PROPOSAL;
                              timeAllocatedToMeetings += meetingLength;
                              }
                           }
                        }
                     outboundMessage.setPerformative(performative);
                     send(outboundMessage);
                     break;
                     }
                  default:
                     {
                     logger.warn("Unexpected message performative:  " + ACLMessage.getPerformative(inboundMessage.getPerformative()));
                     }
                  }
               agendaModificationDateTime = new Date();
            // logger.debug("Agenda modified");
               }
            catch (final IOException ioException)
               {
               logger.error("Unable to set simulated date time content object:  " + ioException.getMessage(),
                            ioException);
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get simulated time content object:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         logger.trace("End   ManageMeetingsBehaviour.action");
         }
      }

   private class MoveBehaviour
      extends CyclicBehaviour
      {
      protected              Date            dateTime         = null;
      private          final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(TimeAgent.timeSimulationOntology);
      private   static final long            serialVersionUID = 1833830971141389258L;

      public MoveBehaviour(final Agent agent)
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
         }

      @Override
      public void action()
         {
         logger.trace("Begin MoveBehaviour.action");

         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            try
               {
               final Date       dateTime        = (Date) inboundMessage.getContentObject();
               final ACLMessage outboundMessage = createOutboundMessage(dateTime);

               if (outboundMessage != null)
                  {
                  logger.debug("Sending movement");
                  outboundMessage.setOntology(movementOntology);
                  send(outboundMessage);
                  }
               this.dateTime = dateTime;
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
         logger.trace("End   MoveBehaviour.action");
         }

      protected ACLMessage createOutboundMessage(final Date dateTime)
         throws IOException
         {
         final ACLMessage aclMessage;

         if (this.dateTime == null || (this.dateTime.getHours() > dateTime.getHours()))
            {
            workStartDateTime = preferredWorkStartDateTime; // TODO:  Add some randomness
            workStopDateTime = preferredWorkStopDateTime; // TODO:  Add some randomness
            for (int minute = 0; minute < meetingRoomAgentName.length; minute++)
               {
               meetingRoomAgentName[minute] = getDefaultWorkSpaceAgentName(minute / 60);
               }
            timeAllocatedToMeetings = 0;
            aclMessage = null;
            }
         else
            {
            final int hour   = dateTime.getHours();
            final int minute = hour * 60 + dateTime.getMinutes();

            if (this.dateTime.getHours() == 0 && hour == 1) // 01:00
               {
               synchronized (PersonAgent.class)
                  {
                  for (int i = 0; i < meetingRoomAgentName.length; i++)
                     {
                     logger.debug("At minute " + i + ", scheduled for " + meetingRoomAgentName[i]);
                     }
                  }
               }
            
         // final int        previousHour               = (hour + meetingRoomAgentName.length - 1) % meetingRoomAgentName.length;
         // final String     enteringWorkSpaceAgentName = meetingRoomAgentName[hour];
         // final String     leavingWorkSpaceAgentName  = meetingRoomAgentName[previousHour];
            final int        previousMinute             = (minute + meetingRoomAgentName.length - 1) % meetingRoomAgentName.length;
            final String     enteringWorkSpaceAgentName = meetingRoomAgentName[minute];
            final String     leavingWorkSpaceAgentName  = meetingRoomAgentName[previousMinute];
                  boolean    messageNeedsToBeSent       = false;
            final ACLMessage outboundMessage            = new ACLMessage(ACLMessage.INFORM);

         // logger.debug("Previous (" + previousHour + "):  " + meetingRoomAgentName[previousHour]);
         // logger.debug("Current  (" + hour         + "):  " + meetingRoomAgentName[hour]);
            if (leavingWorkSpaceAgentName != null)
               {
               messageNeedsToBeSent = true;
               outboundMessage.addReceiver(new AID(leavingWorkSpaceAgentName, AID.ISLOCALNAME));
               }
            if (enteringWorkSpaceAgentName != null)
               {
               messageNeedsToBeSent = true;
               if (!enteringWorkSpaceAgentName.equals(leavingWorkSpaceAgentName))
                  {
                  outboundMessage.addReceiver(new AID(enteringWorkSpaceAgentName, AID.ISLOCALNAME));
                  }
               }
            if (messageNeedsToBeSent)
               {
               outboundMessage.setContentObject(new Movement(dateTime,
                                                             enteringWorkSpaceAgentName,
                                                             leavingWorkSpaceAgentName));
               aclMessage = outboundMessage;
               }
            else
               {
               aclMessage = null;
               }
            }
         // TODO:  lunch
         return aclMessage;
         }
      }

   private class StochasticMovementBehaviour
      extends MoveBehaviour
      {
      private StochasticMovementModel movementModel      = null;
      private Date                    timeOfNextActStart = null;
      private Date                    timeOfNextActEnd   = null;

      public StochasticMovementBehaviour(final Agent agent)
         {
         super(agent);
         movementModel = new StochasticMovementModel(movementModelFile);
         }

      protected ACLMessage createOutboundMessage(final Date dateTime)
         throws IOException
         {
         final ACLMessage aclMessage = null; // needs to be something non-null

         if (this.dateTime == null || (this.dateTime.getHours() > dateTime.getHours()))
            {
            movementModel.initDay(dateTime);
            workStartDateTime = movementModel.getWorkStartTime();
            workStopDateTime = movementModel.getWorkEndTime();
            }
         else
            {
            if ((timeOfNextActEnd == null) || (timeOfNextActEnd.compareTo(dateTime) >= 0))
               {
               this.movementModel.nextActivity();
               this.timeOfNextActStart = this.movementModel.getActvityStartTime();
               int duration = this.movementModel.getActivityDuration();
               WorkdayActivity act = this.movementModel.getActivity();
               Calendar c = Calendar.getInstance();
               c.setTime( this.timeOfNextActStart );
               c.add( Calendar.MINUTE, duration );
               this.timeOfNextActEnd = c.getTime();
               }
            // need to retain previous activity.
            if (timeOfNextActStart.compareTo(dateTime) < 0)
               {
               // entering workspace = home work space
               }
            else
               {
               // entering workspace is dependent on type of activity (movementModel.getActivity())
               }
            }
         // send messages
         return aclMessage;
         }
      }

   private class ReportAgendaBehaviour
      extends TickerBehaviour
      {
      private        final long minimumQuiesenceTime;
      private static final char one                  = '1';
      private static final long serialVersionUID     = -3732074322999794346L;
      private static final char zero                 = '0';

      public ReportAgendaBehaviour(final Agent agent,
                                   final long  period,
                                   final long  minimumQuiesenceTime)
         {
         super(agent,
               period);
         this.minimumQuiesenceTime = minimumQuiesenceTime;
         }

      @Override
      protected void onTick()
         {
         if (agendaModificationDateTime != null)
            {
            if ((agendaReportDateTime == null) ||
                (agendaReportDateTime.before(agendaModificationDateTime)))
               {
               final Date currentDateTime = new Date();

               if (currentDateTime.getTime() - agendaModificationDateTime.getTime() >= minimumQuiesenceTime)
                  {
                  final Map<String, char[]> localityOccupancyMap = new HashMap<>();

                  synchronized (PersonAgent.class){
                  for (int minute = 0; minute < meetingRoomAgentName.length; minute++)
                     {
                     final String localityAgentName = meetingRoomAgentName[minute];

                  // logger.debug("agenda minute:  " + minute + ", locality:  " + localityAgentName);
                     if (localityAgentName != null)
                        {
                        final boolean localityIsKnown = localityOccupancyMap.containsKey(localityAgentName);
                        final char[]  occupied;

                        if (localityIsKnown)
                           {
                           occupied = localityOccupancyMap.get(localityAgentName);
                           }
                        else
                           {
                           occupied = new char[24 * 60];
                           for (int i = 0; i < meetingRoomAgentName.length; i++)
                              {
                              occupied[i] = zero;
                              }
                           }
                        occupied[minute] = one;
                        localityOccupancyMap.put(localityAgentName, occupied);
                     // logger.debug("agenda Map:  " + new String(occupied));
                        }
                     }
                  for (String localityAgentLocalName:  localityOccupancyMap.keySet())
                     {
                     final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     outboundMessage.addReceiver(new AID(localityAgentLocalName, AID.ISLOCALNAME));
                     outboundMessage.setContent(new String(localityOccupancyMap.get(localityAgentLocalName)));
                     outboundMessage.setOntology(agendaOntology);
                     send(outboundMessage);
                     logger.debug("Sending agenda to " + localityAgentLocalName + ": " + outboundMessage.getContent());
                     }}
                  agendaReportDateTime = currentDateTime;
                  }
               }
            }
         }
      }

   private class Meeting
      {
      public         final String meetingRoomAgentLocalName;
      public         final Date   startDateTime;
      public         final Date   stopDateTime;

      public Meeting(final String meetingRoomAgentLocalName,
                     final Date   startDateTime,
                     final Date   stopDateTime)
         {
         this.meetingRoomAgentLocalName = meetingRoomAgentLocalName;
         this.startDateTime = startDateTime;
         this.stopDateTime = stopDateTime;
         }
      }

   public static class Movement
      implements Serializable
      {
      public         final Date   dateTime;
      public         final String enteringRoomAgentName;
      public         final String leavingRoomAgentName;
      private static final long   serialVersionUID      = -4350548970268564453L;

      public Movement(final Date   dateTime,
                      final String enteringRoomAgentName,
                      final String leavingRoomAgentName)
         {
         this.dateTime = dateTime;
         this.enteringRoomAgentName = enteringRoomAgentName;
         this.leavingRoomAgentName = leavingRoomAgentName;
         }
      }
   }
