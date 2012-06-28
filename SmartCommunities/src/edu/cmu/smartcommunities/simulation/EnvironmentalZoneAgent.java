package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.database.controller.LocalityDAOInterface;
import edu.cmu.smartcommunities.database.controller.LocalityManager;
import edu.cmu.smartcommunities.database.controller.hibernate.DAOFactory;
import edu.cmu.smartcommunities.database.model.Locality;
//import edu.cmu.smartcommunities.database.model.Measurement;
import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumer;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumerInterface;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class EnvironmentalZoneAgent
   extends Agent
   {
   private   static final String                       className                       = EnvironmentalZoneAgent.class.getName();
// private   static final DAOFactory                   daoFactory                      = new DAOFactory();
   private          final DateFormat                   dateFormat                      = new SimpleDateFormat("yyyyMMddHHmm");
   protected              AID                          electricityMeterAgentId         = new AID();
   private                ElectricityConsumerInterface electricityConsumer             = null;
   private                int                          energizedElectricalLoad         = 0;
// private                Locality                     locality                        = null;
// private          final LocalityDAOInterface         localityDAO                     = daoFactory.getLocalityDAO();
   private                long                         localityId                      = 0;
// private          final LocalityManager              localityManager                 = new LocalityManager();
   protected              int                          occupancy                       = 0;
   protected              Date                         occupancyChangeDateTime         = null;
   public    static final String                       occupancyOntology               = className + ":Occupancy";
   private   static final MessageTemplate              messageTemplate                 = MessageTemplate.MatchOntology(WorkSpaceAgent.occupancyOntology);
   private                Date                         mostRecentMovementDateTime      = null;
   private   static       Date                         mostRecentObservationDateTime   = null;
   private                AID                          parentEnvironmentalZoneAgentId  = new AID();
   private   static final String                       put                             = "PUT";
// private   static       boolean                      reportOccupancyBehaviourCreated = false;
   private   static final long                         serialVersionUID                = -5380890545320114277L;
   private                String                       servletUrl                      = null;
   private   static final String                       unknown                         = "Unknown";
   private          final AID                          visualizationAgentId            = new AID();

   protected void setup()
      {
      logger.info("Begin setup");
      super.setup();
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      electricityConsumer = ElectricityConsumer.getElectricityConsumer(getLocalName());
      electricityMeterAgentId.setLocalName(extendedProperties.getProperty("ElectricityMeterAgentLocalName", "ElectricityMeterAgent"));
      energizedElectricalLoad = extendedProperties.getIntProperty("EnergizedElectricalLoad", 0);
      visualizationAgentId.setLocalName(extendedProperties.getProperty("VisualizationAgentLocalName", "VisualizationAgent"));

      final String parentEnvironmentalZoneAgentLocalName = extendedProperties.getProperty("ParentEnvironmentalZoneAgentLocalName", unknown);

      if (parentEnvironmentalZoneAgentLocalName.equals(unknown))
         {
         parentEnvironmentalZoneAgentId = null;
         }
      else
         {
         parentEnvironmentalZoneAgentId.setLocalName(parentEnvironmentalZoneAgentLocalName);
         }

      final String localityName = extendedProperties.getProperty("Locality");
      logger.info("Locality:  " + localityName);
      if ((localityId = extendedProperties.getIntProperty("LocalityId", 0)) == 0)
         {
         logger.error("Unable to determine localityId");
         }
      else
   // if ((locality = localityManager.getLocality(localityName)) == null)
   //    {
   //    logger.error("Unable to find locality " + localityName);
   //    }
   // else
         {
         if (localityId != ((ElectricityConsumer) electricityConsumer).id)
            {
            throw new IllegalStateException("Id mismatch:  " + localityId + " " + ((ElectricityConsumer) electricityConsumer).id);
            }
         if ((servletUrl = extendedProperties.getProperty("ServletUrl")) == null)
            {
            logger.error("Unable to locate the servlet URL");
            }
         else
            {
            addBehaviour(new MonitorOccupancyBehaviour(this));
         // synchronized (EnvironmentalZoneAgent.class)
         //    {
         //    if (!reportOccupancyBehaviourCreated)
         //       {
                  addBehaviour(new ReportOccupancyBehaviour(this, 100, 20000));
         //       reportOccupancyBehaviourCreated = true;
         //       }
         //    }
            }
         }
      logger.info("End   setup");
      }

   private class MonitorOccupancyBehaviour
      extends CyclicBehaviour
      {
      private static final long serialVersionUID = 9137304799541502421L;

      public MonitorOccupancyBehaviour(final Agent agent)
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
            try
               {
               mostRecentMovementDateTime = (Date) inboundMessage.getContentObject();

            // final State state = localityManager.getState(locality,
            //                                              mostRecentMovementDateTime);

            // occupancy = state.getCumulativeOccupancy();
               occupancy = electricityConsumer.getTotalOccupancy(mostRecentMovementDateTime,
                                                                 new Date(mostRecentMovementDateTime.getTime() + 1));
            // logger.debug("Received occupancy report for " +
            //              new SimpleDateFormat("yyyy-MM-dd HH:mm").format(mostRecentMovementDateTime) +
            //              ", occupancy:  " +
            //              occupancy);
            // logger.debug(electricityConsumer.toString() +
            //              " @ " +
            //              new SimpleDateFormat("yyyy-MM-dd HH:mm").format(mostRecentMovementDateTime) +
            //              ":  SETTING WATTS TO " +
            //              (occupancy > 0 ? energizedElectricalLoad : 0));
               electricityConsumer.setElectricityConsumption(occupancy > 0 ? energizedElectricalLoad : 0,
                                                             mostRecentMovementDateTime);
               /*
               if (parentEnvironmentalZoneAgentId != null)
                  {
                  final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                  logger.debug("Sending occupancy up to " + parentEnvironmentalZoneAgentId.getLocalName());
                  outboundMessage.addReceiver(parentEnvironmentalZoneAgentId);
                  outboundMessage.setContentObject(mostRecentMovementDateTime);
                  outboundMessage.setOntology(WorkSpaceAgent.occupancyOntology);
                  send(outboundMessage);
                  }
               */
               synchronized (EnvironmentalZoneAgent.class)
                  {
                  mostRecentObservationDateTime = new Date();
                  }
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get occupancy change content:  " + unreadableException.getMessage(),
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
         }
      }

   public class ReportOccupancyBehaviour
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
      // logger.debug("TICK 1");
         if (mostRecentMovementDateTime != null)
            {
         // logger.debug("TICK 2");
            synchronized (EnvironmentalZoneAgent.class)
               {
            // logger.debug("TICK 3");
               if ((mostRecentReportDateTime == null) || 
                   (mostRecentObservationDateTime.after(mostRecentReportDateTime)))
                  {
               // logger.debug("TICK 4");
                  final Date currentDateTime = new Date();

                  if (currentDateTime.getTime() - mostRecentObservationDateTime.getTime() >= minimumQuiescenceTime)
                     {
                     logger.debug("Reporting OCCUPANCY");
                     final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     outboundMessage.addReceiver(visualizationAgentId);
                     outboundMessage.setOntology(occupancyOntology);
                     send(outboundMessage);
                     mostRecentReportDateTime = currentDateTime;

                  // final Date              beginDateTime2    = mostRecentObservationDateTime;
                     final Calendar beginDateTime = new GregorianCalendar(TimeZone.getDefault());

                     beginDateTime.setTime(mostRecentMovementDateTime);
                     beginDateTime.set(Calendar.HOUR_OF_DAY, 0);
                     beginDateTime.set(Calendar.MINUTE, 0);
                     beginDateTime.set(Calendar.SECOND, 0);
                     beginDateTime.set(Calendar.MILLISECOND, 0);
                     beginDateTime.add(Calendar.MINUTE, -1);
                     for (int minute = 0; minute < 24 * 60; minute++)
                        {
                        beginDateTime.add(Calendar.MINUTE, 1);

                        final Calendar endDateTime = (Calendar) beginDateTime.clone();

                        endDateTime.add(Calendar.MILLISECOND, 1);
                        try
                           {
                        // final int               localOccupancy    = electricityConsumer.getTotalOccupancy(mostRecentObservationDateTime,
                        //                                                                                   new Date(mostRecentObservationDateTime.getTime() + 1));
                        // final Date              endDateTime       = new Date(mostRecentObservationDateTime.getTime() + 1);
                           final int               occupancy         = electricityConsumer.getOccupancy(beginDateTime.getTime(),
                                                                                                        endDateTime.getTime());
                           final Double            watts             = (double) electricityConsumer.getTotalElectricityConsumption(beginDateTime.getTime(),
                                                                                                                                   endDateTime.getTime());
                           if (occupancy != 0 || watts != 0)
                              {
                              final URL               url               = new URL(servletUrl +
                                                                                  "?localityId=" + localityId +
                                                                                  "&measurementDateTime=" + dateFormat.format(beginDateTime.getTime()) +
                                                                                  "&occupancy=" + occupancy +
                                                                                  "&watts=" + watts);
                              final HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();

                              httpUrlConnection.setDoOutput(true);
                              httpUrlConnection.setRequestMethod(put);

                              final int responseCode = httpUrlConnection.getResponseCode();

                              if (responseCode != HttpURLConnection.HTTP_OK)
                                 {
                                 logger.error("Servlet response code = " + responseCode + ", URL:  " + url);
                                 }
                              }
                           }
                        catch (final MalformedURLException malformedUrlException)
                           {
                           logger.error("Cannot create servlet URL", malformedUrlException);
                           }
                        catch (final IOException ioException)
                           {
                           logger.error("Error in communicating with servlet", ioException);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   public static class ElectricityConsumption
      implements Serializable
      {
      public         final Date dateTime;
      public         final int  electricityConsumed;
      private static final long serialVersionUID    = 1421390844346450004L;

      public ElectricityConsumption(final Date dateTime,
                                    final int  electricityConsumed)
         {
         this.dateTime = dateTime;
         this.electricityConsumed = electricityConsumed;
         }
      }
   }
