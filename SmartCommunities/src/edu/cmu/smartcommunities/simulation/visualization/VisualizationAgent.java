package edu.cmu.smartcommunities.simulation.visualization;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.simulation.EnvironmentalZoneAgent;
import edu.cmu.smartcommunities.simulation.TimeAgent;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumer;
import edu.cmu.smartcommunities.simulation.model.ElectricityConsumerInterface;
import edu.cmu.smartcommunities.simulation.model.SimulationModel;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class VisualizationAgent
   extends Agent
   {
   private        final MessageTemplate messageTemplate = MessageTemplate.MatchOntology(EnvironmentalZoneAgent.occupancyOntology);
   private              SimulationModel simulationModel = SimulationModel.getInstance();

   public static void main(String[] args)
      {
      new VisualizationAgent().setup();
      }

   protected void setup()
      {
      logger.trace("Begin setup");
      super.setup();
      try
         {
         simulationModel.setStartDateTime(new SimpleDateFormat(TimeAgent.iso8601DateTimeFormat).parse(extendedProperties.getProperty("StartDateTime", "2012-01-03 00:00:00")));
         new VisualizationFrame(simulationModel).setVisible(true);
         addBehaviour(new MonitorActivityBehaviour(this));
         }
      catch (final ParseException parseException)
         {
         logger.error("Unable to parse StartDate.");
         }
      logger.trace("End   setup");
      }

   @Override
   protected void takeDown()
      {
      logger.info("Begin writing model to disc");
      SimulationModel.writeToDisc();
      logger.info("End   writing model to disc");
      }

   private class MonitorActivityBehaviour
      extends CyclicBehaviour
      {
      public MonitorActivityBehaviour(final Agent agent)
         {
         super(agent);
         }

      @Override
      public void action()
         {
         logger.trace("Begin action");

         final ACLMessage inboundMessage = receive(messageTemplate);

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            final String                       agentLocalName      = inboundMessage.getSender().getLocalName();
            final ElectricityConsumerInterface electricityConsumer = ElectricityConsumer.getElectricityConsumer(agentLocalName);

            if (electricityConsumer == null)
               {
               logger.error("Unable to locate the electricity consumer related to " + agentLocalName);
               }
            else
               {
               simulationModel.fireTreeNodesChanged(electricityConsumer);
               }
            }
         logger.trace("End   action");
         }
      }
   }
