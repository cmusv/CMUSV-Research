package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.database.controller.LocalityManager;
import edu.cmu.smartcommunities.database.controller.MeasurementManager;
import edu.cmu.smartcommunities.database.model.Locality;
import edu.cmu.smartcommunities.database.model.Measurement;
import edu.cmu.smartcommunities.database.model.Sensor;
import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.List;
import java.util.Vector;

public class EnvironmentalZoneAgent
   extends Agent
   {
   private static final String   className         = EnvironmentalZoneAgent.class.getName();
   private              Locality locality          = null;
   public  static final String   occupancyOntology = className + ":Occupancy";
   private              long     sensorId          = 0;
   private static final long     serialVersionUID  = -5380890545320114277L;

   protected void setup()
      {
      logger.trace("Begin setup");
      super.setup();

      final long            localityId      = extendedProperties.getIntProperty("LocalityId", 0);
      final LocalityManager localityManager = new LocalityManager();

      if ((locality = localityManager.getById(localityId)) == null)
         {
         logger.error("Unable to determine locality");
         }
      else
         {
         final Sensor sensor = localityManager.getSensor(localityId,
                                                         "simulatedWatts");

         if (sensor != null)
            {
            sensorId = sensor.getId();
            }
         addBehaviour(new ProvideLocalityBehaviour(this));
         addBehaviour(new MonitorOccupancyBehaviour(this));
         }
      logger.trace("End   setup");
      }

   private class MonitorOccupancyBehaviour
      extends CyclicBehaviour
      {
      private        final MeasurementManager measurementManager = new MeasurementManager();
      private        final MessageTemplate    messageTemplate    = MessageTemplate.MatchOntology(WorkSpaceAgent.occupancyOntology);
      private static final long               serialVersionUID   = 9137304799541502421L;

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
               messagesReceived++;

               final Measurement[]     occupancyMeasurementArray = (Measurement[]) inboundMessage.getContentObject();
               final List<Measurement> wattsMeasurementList      = new Vector<>(occupancyMeasurementArray.length);

               for (final Measurement occupancyMeasurement:  occupancyMeasurementArray)
                  {
                  final Measurement wattsMeasurement = new Measurement();

                  wattsMeasurement.setMeasurementDateTime(occupancyMeasurement.getMeasurementDateTime());
                  wattsMeasurement.setValue(occupancyMeasurement.getValue() == 0 ? 0 : locality.getWatts());
                  wattsMeasurementList.add(wattsMeasurement);
                  }
               if (sensorId != 0)
                  {
                  measurementManager.putMeasurements(wattsMeasurementList,
                                                     sensorId);
                  }
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get occupancy change content:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         }
      }

   private class ProvideLocalityBehaviour
      extends CyclicBehaviour
      {
      private        final MessageTemplate messageTemplate  = MessageTemplate.MatchOntology(WorkSpaceAgent.localityOntology);
      private static final long            serialVersionUID = -5690323535511047935L;

      public ProvideLocalityBehaviour(final Agent agent)
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
            final ACLMessage outboundMessage = inboundMessage.createReply();

            messagesReceived++;
            outboundMessage.setContent(Long.toString(locality.getId()));
            outboundMessage.setPerformative(ACLMessage.INFORM);
            send(outboundMessage);
            messagesSent++;
            }
         }
      }
   }
