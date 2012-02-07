package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
// import edu.cmu.smartcommunities.utilities.Parser;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.Date;

public abstract class EnvironmentalZoneAgent
   extends Agent
   {
   private   static final String          className               = EnvironmentalZoneAgent.class.getName();
   protected              AID             electricMeterAgentId    = new AID();
   private                boolean         energized               = false;
   private                int             energizedElectricalLoad = 0;
   protected              int             occupancy               = 0;
   protected              Date            occupancyChangeDateTime = null;
   public    static final String          occupancyOntology       = className + ":Occupancy";
   private   static final MessageTemplate messageTemplate         = MessageTemplate.MatchOntology(occupancyOntology);
   private   static final long            serialVersionUID        = -5380890545320114277L;
   private          final AID             visualizationAgentId = new AID("mcsmith-mcsmith-VisualizationAgent", AID.ISLOCALNAME); // TODO:  don't leave this hardcoded
   
   protected void setup()
      {
      super.setup();
      electricMeterAgentId.setLocalName(extendedProperties.getProperty("ElectricityMeterAgentName", "mcsmith-mcsmith-ElectricityMeterAgent"));  // TODO:  Don't hardcode this
      energizedElectricalLoad = extendedProperties.getIntProperty("EnergizedElectricalLoad", 0);
      addBehaviour(new MonitorOccupancyBehaviour(this));
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
            // final Date   occupancyDateTime = new Date();
            // final Parser parser            = new Parser(inboundMessage.getContent());
               final WorkSpaceAgent.OccupancyChange occupancyChange = (WorkSpaceAgent.OccupancyChange) inboundMessage.getContentObject();
               
            // occupancy += Integer.parseInt(parser.getToken(1));
               occupancy += occupancyChange.deltaOccupancy;
            // occupancyDateTime.setTime(Long.parseLong(parser.getToken(2)));
               occupancyChangeDateTime = occupancyChange.simulatedDateTime;
               logger.debug("Occupancy:  " + occupancy);

               final boolean energized = occupancy > 0;

               if (EnvironmentalZoneAgent.this.energized != energized)
                  {
                  try
                     {
                     final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

                     outboundMessage.addReceiver(electricMeterAgentId);
                     outboundMessage.addReceiver(visualizationAgentId);
                  // outboundMessage.setContent("(energyUse (device " + getName() + ") (load " + (energized ? energizedElectricalLoad : 0) + ") (time " + occupancyChangeDateTime.getTime() + ")");
                     outboundMessage.setContentObject(new ElectricityMeterAgent.Usage(energized ? energizedElectricalLoad : 0,
                                                                                      occupancyChangeDateTime));
                     outboundMessage.setOntology(ElectricityMeterAgent.electricalConsumptionOntology);
                     send(outboundMessage);
                     EnvironmentalZoneAgent.this.energized = energized;
                     }
                  catch (final IOException ioException)
                     {
                     logger.error("Unable to set electrical usage content object:  " + ioException.getMessage(),
                                  ioException);
                     }
                  }

               final ACLMessage outboundMessage = new ACLMessage(ACLMessage.INFORM);

               outboundMessage.addReceiver(visualizationAgentId);
               outboundMessage.setContentObject(occupancyChange);
               outboundMessage.setOntology(occupancyOntology);
               send(outboundMessage);
               }
            catch (final IOException ioException)
               {
               logger.error("Unable to set occupancy change content:  " + ioException.getMessage(),
                            ioException);
               }
            catch (final UnreadableException unreadableException)
               {
               logger.error("Unable to get occupancy change content:  " + unreadableException.getMessage(),
                            unreadableException);
               }
            }
         }
      }
   }
