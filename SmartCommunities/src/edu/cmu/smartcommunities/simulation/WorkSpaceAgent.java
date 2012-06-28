package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.database.controller.LocalityDAOInterface;
import edu.cmu.smartcommunities.database.controller.LocalityManager;
import edu.cmu.smartcommunities.database.controller.hibernate.DAOFactory;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumer;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumerInterface;
import edu.cmu.smartcommunities.simulation.model.SimulationModel;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class WorkSpaceAgent
   extends Agent
   {
   protected              int                          capacity;
   private   static final String                       className                     = WorkSpaceAgent.class.getName();
   protected        final AID                          coolingZoneAgentId            = new AID();
// private   static final DAOFactory                   daoFactory                    = new DAOFactory();
   private                ElectricityConsumerInterface electricityConsumer           = null;
   protected        final AID                          heatingZoneAgentId            = new AID();
   protected        final AID                          lightingZoneAgentId           = new AID();
   protected              String                       localName                     = null;
   protected              Locality                     locality                      = null;
// private          final LocalityDAOInterface         localityDAO                   = daoFactory.getLocalityDAO();
// private          final LocalityManager              localityManager               = new LocalityManager();
   private                Date                         mostRecentMovementDateTime    = null;
   private   static       Date                         mostRecentObservationDateTime = null;
   private                Date                         occupancyDateTime             = null;
   private                Date                         occupancyModificationDateTime = null;
   public    static final String                       occupancyOntology             = className + ":Occupancy";
   private                Date                         occupancyReportDateTime       = null;
   protected        final Set<String>                  occupantSet                   = new HashSet<String>();
   private          final List<String>                 personOccupancyList           = new Vector<>();
   private   static final long                         serialVersionUID              = -7605004311858914283L;
   private                Date                         simulatedDateTime             = null;

   protected void setup()
      {
      super.setup();
      capacity = extendedProperties.getIntProperty("Capacity", 0);
      localName = getLocalName();
      coolingZoneAgentId.setLocalName(extendedProperties.getProperty("CoolingZoneAgentLocalName", "Unknown cooling zone agent for " + localName));
      heatingZoneAgentId.setLocalName(extendedProperties.getProperty("HeatingZoneAgentLocalName", "Unknown heating zone agent for " + localName));

      final String lightingZoneAgentLocalName = extendedProperties.getProperty("LightingZoneAgentLocalName", "Unknown lighting zone agent for " + localName);

      // Force the instantiation of the model singleton, if it hasn't bee done yet.
      SimulationModel.getInstance();
      lightingZoneAgentId.setLocalName(lightingZoneAgentLocalName);
      electricityConsumer = ElectricityConsumer.getElectricityConsumer(lightingZoneAgentLocalName);
   // locality = localityDAO.getLocality(extendedProperties.getProperty("Locality"));
   // locality = localityManager.getLocality(extendedProperties.getProperty("Locality"));
      addBehaviour(new MonitorAgendasBehaviour(this));
   // addBehaviour(new MonitorOccupancyBehaviour(this));
   // addBehaviour(new ReportOccupancyBehaviour(this,
   //                                           100,
   //                                           2000));
      addBehaviour(new MonitorTimeBehaviour(this));
      addBehaviour(new SummarizeOccupancyBehaviour(this,
                                                   100,
                                                   20000));
      }

   private class MonitorAgendasBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(PersonAgent.agendaOntology);

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
            synchronized (personOccupancyList)
               {
               personOccupancyList.add(inboundMessage.getContent());
               }
            logger.debug("Received " + inboundMessage.getContent() + " from " + inboundMessage.getSender().getLocalName());
            occupancyModificationDateTime = new Date();
            }
         }
      }

   private class MonitorOccupancyBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(PersonAgent.movementOntology);
      private static final long            serialVersionUID = 4790328090611231047L;

      public MonitorOccupancyBehaviour(final Agent agent)
         {
         super(agent);
         logger.trace("Begin MonitorOccupancyBehaviour.<init>");
         logger.trace("End   MonitorOccupancyBehaviour.<init>");
         }

      @Override
      public void action()
         {
         logger.trace("Begin MonitorOccupancyBehaviour.action");

         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            try
               {
               final PersonAgent.Movement movement        = (PersonAgent.Movement) inboundMessage.getContentObject();  
               final String               senderAgentName = inboundMessage.getSender().getLocalName();

               if (localName.equals(movement.leavingRoomAgentName))
                  {
                  occupantSet.remove(senderAgentName);
                  }
               if (localName.equals(movement.enteringRoomAgentName))
                  {
                  occupantSet.add(senderAgentName);
                  }
               mostRecentMovementDateTime = movement.dateTime;
               electricityConsumer.setOccupancy(occupantSet.size(),
                                                mostRecentMovementDateTime);
               synchronized (WorkSpaceAgent.class)
                  {
                  mostRecentObservationDateTime = new Date();
                  }

               /*
               final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

            // outboundMessage.addReceiver(coolingZoneAgentId);
            // outboundMessage.addReceiver(heatingZoneAgentId);
               outboundMessage.addReceiver(lightingZoneAgentId);
               outboundMessage.setContentObject(mostRecentMovementDateTime);
               outboundMessage.setOntology(occupancyOntology);
               send(outboundMessage);
               */
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get simulated time content object:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            /*
            catch (final IOException ioException)
               {
               logger.error("Unable to set occupancy change content:  " + ioException.getMessage(),
                            ioException);
               }
            */
            }
         logger.trace("End   MonitorOccupancyBehaviour.action");
         }
      }

   private class MonitorTimeBehaviour
      extends CyclicBehaviour
      {
      private final MessageTemplate messageTemplate = MessageTemplate.MatchOntology(TimeAgent.timeSimulationOntology);

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
            try
               {
               simulatedDateTime = (Date) inboundMessage.getContentObject();
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get simulated time content object:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         }
      }

   private class ReportOccupancyBehaviour
      extends TickerBehaviour
      {
      private final int  minimumQuiescenceTime;
      private       Date mostRecentReportDateTime = null;

      public ReportOccupancyBehaviour(final Agent agent,
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
      // logger.debug("Tick 1");
         if (mostRecentMovementDateTime != null)
            {
         // logger.debug("Tick 2");
            synchronized (WorkSpaceAgent.class)
               {
            // logger.debug("Tick 3");
               if ((mostRecentReportDateTime == null) || 
                   (mostRecentObservationDateTime.after(mostRecentReportDateTime)))
                  {
               // logger.debug("Tick 4");
                  final Date currentDateTime = new Date();

                  if (currentDateTime.getTime() - mostRecentObservationDateTime.getTime() >= minimumQuiescenceTime)
                     {
                     try
                        {
                        logger.debug("Reporting occupancy of " + occupantSet.size() + " at " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(mostRecentMovementDateTime) + " to " + lightingZoneAgentId.getLocalName());
                        electricityConsumer.setOccupancy(occupantSet.size(),
                                                         mostRecentMovementDateTime);
                     // localityManager.setLocalOccupancy(locality,
                     //                                   mostRecentMovementDateTime,
                     //                                   occupantSet.size());

                        final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     // outboundMessage.addReceiver(coolingZoneAgentId);
                     // outboundMessage.addReceiver(heatingZoneAgentId);
                        outboundMessage.addReceiver(lightingZoneAgentId);
                        outboundMessage.setContentObject(mostRecentMovementDateTime);
                        outboundMessage.setOntology(occupancyOntology);
                        send(outboundMessage);
                        mostRecentReportDateTime = currentDateTime;
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

   private class SummarizeOccupancyBehaviour
      extends TickerBehaviour
      {
      private final int  minimumQuiescenceTime;
      private       Date mostRecentReportDateTime = null;

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
         if (occupancyModificationDateTime != null)
            {
         // logger.debug("Tick 2");
            synchronized (WorkSpaceAgent.class)
               {
            // logger.debug("Tick 3");
               if ((occupancyReportDateTime == null) || 
                   (occupancyModificationDateTime.after(occupancyReportDateTime)))
                  {
               // logger.debug("Tick 4");
                  final Date currentDateTime = new Date();

                  if (currentDateTime.getTime() - occupancyModificationDateTime.getTime() >= minimumQuiescenceTime)
                     {
                     try
                        {
                        synchronized (personOccupancyList)
                           {
                           final int      personCount     = personOccupancyList.size();
                           final char[][] personOccupancy = new char[personCount][];
                                 int      person          = 0;

                           for (String personOccupancyString:  personOccupancyList)
                              {
                              personOccupancy[person++] = personOccupancyString.toCharArray();
                              }
                           for (int minute = 0; minute < 24 * 60; minute++)
                              {
                              int occupants = 0;

                              for (person = 0; person < personCount; person++)
                                 {
                                 if (personOccupancy[person][minute] != '0')
                                    {
                                    occupants++;
                                    }
                                 }

                              final Calendar occupancyDateTime = new GregorianCalendar();

                              occupancyDateTime.setTime(simulatedDateTime);
                              occupancyDateTime.set(Calendar.HOUR_OF_DAY, 0);
                              occupancyDateTime.set(Calendar.MINUTE, 0);
                              occupancyDateTime.set(Calendar.SECOND, 0);
                              occupancyDateTime.set(Calendar.MILLISECOND, 0);
                              occupancyDateTime.add(Calendar.MINUTE, minute);
                           // logger.debug("Reporting occupancy of " + occupants + " at " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(occupancyDateTime.getTime()) + " to " + lightingZoneAgentId.getLocalName());
                              electricityConsumer.setOccupancy(occupants,
                                                               occupancyDateTime.getTime());
                           // localityManager.setLocalOccupancy(locality,
                           //                                   mostRecentMovementDateTime,
                           //                                   occupantSet.size());

                              final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                           // outboundMessage.addReceiver(coolingZoneAgentId);
                           // outboundMessage.addReceiver(heatingZoneAgentId);
                              outboundMessage.addReceiver(lightingZoneAgentId);
                              outboundMessage.setContentObject(occupancyDateTime.getTime());
                              outboundMessage.setOntology(occupancyOntology);
                              send(outboundMessage);
                              }
                           occupancyReportDateTime = currentDateTime;
                           personOccupancyList.clear();
                           }
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

   public class Occupancy
      implements Serializable
      {
      public         final Date dateTime;
      public         final int  occupancy;
      private static final long serialVersionUID = 558626552696674876L;

      public Occupancy(final Date dateTime,
                       final int  occupancy)
         {
         this.dateTime = dateTime;
         this.occupancy = occupancy;
         }
      }
   }
