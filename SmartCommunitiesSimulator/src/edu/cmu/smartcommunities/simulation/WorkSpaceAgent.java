package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.database.controller.LocalityManager;
import edu.cmu.smartcommunities.database.controller.MeasurementManager;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import edu.cmu.smartcommunities.database.model.Sensor;
import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class WorkSpaceAgent
   extends Agent
   {
   private   static final String                       className                     = WorkSpaceAgent.class.getName();
// protected        final AID                          coolingZoneAgentId            = new AID();
   private                Calendar                     dateTime                      = null;
// protected        final AID                          heatingZoneAgentId            = new AID();
   protected        final AID                          lightingZoneAgentId           = new AID();
// protected              String                       localName                     = null;
   protected              Locality                     locality                      = null;
// private                long                         localityId                    = 0;
   public    static final String                       localityOntology              = className + ":Locality";
   private                Calendar                     occupancyModificationDateTime = null;
   public    static final String                       occupancyOntology             = className + ":Occupancy";
   protected        final Set<String>                  occupantSet                   = new HashSet<String>();
   private          final List<String>                 personOccupancyList           = new Vector<>();
   private                long                         sensorId                      = 0;
   private   static final long                         serialVersionUID              = -7605004311858914283L;

   protected void setup()
      {
      super.setup();
   // localName = getLocalName();
   // coolingZoneAgentId.setLocalName(extendedProperties.getProperty("CoolingZoneAgentLocalName", "Unknown cooling zone agent for " + localName));
   // heatingZoneAgentId.setLocalName(extendedProperties.getProperty("HeatingZoneAgentLocalName", "Unknown heating zone agent for " + localName));

      final String lightingZoneAgentLocalName = extendedProperties.getProperty("LightingZoneAgentLocalName", "Unknown lighting zone agent for " + getLocalName());

      // Force the instantiation of the model singleton, if it hasn't bee done yet.
   // SimulationModel.getInstance();
      lightingZoneAgentId.setLocalName(lightingZoneAgentLocalName);
      addBehaviour(new RequestLocalityIdBehaviour(this,
                                                  lightingZoneAgentLocalName));
      addBehaviour(new ProcessLocalityIdResponseBehaviour(this));
      addBehaviour(new MonitorAgendasBehaviour(this));
      addBehaviour(new MonitorTimeBehaviour(this));
      addBehaviour(new SummarizeOccupancyBehaviour(this,
                                                   100,
                                                   20000));
      }

   private class MonitorAgendasBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(PersonAgent.agendaOntology);
      private static final long            serialVersionUID = -8986166465164323963L;

      public MonitorAgendasBehaviour(final Agent agent)
         {
         super(agent);
         }

      @Override
      public void action()
         {
         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            messagesReceived++;
            synchronized (personOccupancyList)
               {
               personOccupancyList.add(inboundMessage.getContent());
               }
            logger.debug("Received " + inboundMessage.getContent() + " from " + inboundMessage.getSender().getLocalName());
            occupancyModificationDateTime = Calendar.getInstance();
            }
         }
      }

   private class MonitorTimeBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(TimeAgent.timeSimulationOntology);
      private static final long            serialVersionUID = -881341903122253000L;

      public MonitorTimeBehaviour(final Agent agent)
         {
         super(agent);
         try
            {
            TopicManagementHelper topicManagementHelper = null;

            try
               {
               topicManagementHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
               }
            catch (final ServiceException serviceException)
               {
               logger.error("Unable to obtain the topic management helper.",
                            serviceException);
               }
            if (topicManagementHelper != null)
               {
               topicManagementHelper.register(topicManagementHelper.createTopic(TimeAgent.serviceType));
               }
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
         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            messagesReceived++;
            try
               {
               dateTime = (Calendar) inboundMessage.getContentObject();
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get simulated time content object:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         }
      }

   private class ProcessLocalityIdResponseBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(localityOntology);
      private static final long            serialVersionUID = -521516624972340733L;

      public ProcessLocalityIdResponseBehaviour(final Agent agent)
         {
         super(agent);
         }

      @Override
      public void action()
         {
         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            messagesReceived++;
            final Sensor sensor = new LocalityManager().getSensor(Long.valueOf(inboundMessage.getContent()),
                                                                  "simulatedOccupancy");

            if (sensor != null)
               {
               sensorId = sensor.getId();
               }
            }
         }
      }

   private class RequestLocalityIdBehaviour
      extends OneShotBehaviour
      {
      private        final String lightingZoneAgentLocalName;
      private static final long   serialVersionUID           = -1637670819362603007L;

      public RequestLocalityIdBehaviour(final Agent  agent,
                                        final String lightingZoneAgentLocalName)
         {
         super(agent);
         this.lightingZoneAgentLocalName = lightingZoneAgentLocalName;
         }

      @Override
      public void action()
         {
         final ACLMessage outboundMessage = new ACLMessage(ACLMessage.REQUEST);

         outboundMessage.addReceiver(new AID(lightingZoneAgentLocalName, AID.ISLOCALNAME));
         outboundMessage.setOntology(localityOntology);
         send(outboundMessage);
         messagesSent++;
      // logger.debug("Sent localityId request");
         }
      }

   private class SummarizeOccupancyBehaviour
      extends TickerBehaviour
      {
      private        final MeasurementManager measurementManager      = new MeasurementManager();
      private        final int                minimumQuiescenceTime;
      private              Calendar           occupancyReportDateTime = null;
      private static final long               serialVersionUID        = 7835471168700076034L;

      public SummarizeOccupancyBehaviour(final Agent agent,
                                         final long  period,
                                         final int   minimumQuiescenceTime)
         {
         super(agent,
               period);
         this.minimumQuiescenceTime = minimumQuiescenceTime;
         }

      @Override
      protected void onTick()
         {
         if ((occupancyModificationDateTime != null) &&
             (sensorId != 0))
            {
            if ((occupancyReportDateTime == null) || 
                (occupancyModificationDateTime.after(occupancyReportDateTime)))
               {
               final Calendar currentDateTime = Calendar.getInstance();

               if (currentDateTime.getTime().getTime() - occupancyModificationDateTime.getTime().getTime() >= minimumQuiescenceTime)
                  {
                  try
                     {
                     final List<Measurement> measurementList = new Vector<>();
                     final int               personCount     = personOccupancyList.size();
                     final char[][]          personOccupancy = new char[personCount][];
                           int               person          = 0;
                     final char              occupied        = '1';

                     for (final String personOccupancyString:  personOccupancyList)
                        {
                        personOccupancy[person++] = personOccupancyString.toCharArray();
                        }
                     for (int minute = 0; minute < 24 * 60; minute++)
                        {
                        int occupants = 0;

                        for (person = 0; person < personCount; person++)
                           {
                           if (personOccupancy[person][minute] == occupied)
                              {
                              occupants++;
                              }
                           }
                        if (occupants > 0)
                           {
                           final Measurement measurement       = new Measurement();
                           final Calendar    occupancyDateTime = (Calendar) dateTime.clone();

                           occupancyDateTime.set(Calendar.HOUR_OF_DAY, 0);
                           occupancyDateTime.set(Calendar.MINUTE, 0);
                           occupancyDateTime.set(Calendar.SECOND, 0);
                           occupancyDateTime.set(Calendar.MILLISECOND, 0);
                           occupancyDateTime.add(Calendar.MINUTE, minute);
                           measurement.setMeasurementDateTime(occupancyDateTime.getTime());
                           measurement.setValue(occupants);
                           measurementList.add(measurement);
                           }
                        }
                     measurementManager.putMeasurements(measurementList,
                                                        sensorId);

                     final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     outboundMessage.addReceiver(lightingZoneAgentId);
                     outboundMessage.setContentObject(measurementList.toArray(new Measurement[]{}));
                     outboundMessage.setOntology(occupancyOntology);
                     send(outboundMessage);
                     messagesSent++;
                     occupancyReportDateTime = currentDateTime;
                     personOccupancyList.clear();
                     }
                  catch (final IOException ioException)
                     {
                     logger.error("Unable to set occupancy change content:  " + ioException.getMessage(),
                                  ioException);
                     }
                  }
               }
            }
         }
      }
   }
