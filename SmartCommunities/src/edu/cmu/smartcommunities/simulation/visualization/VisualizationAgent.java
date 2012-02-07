package edu.cmu.smartcommunities.simulation.visualization;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.simulation.EnvironmentalZoneAgent;
import edu.cmu.smartcommunities.simulation.WorkSpaceAgent;
import jade.content.frame.Frame;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * A TreeTable example, showing a JTreeTable, operating on the local file
 * system.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 */

public class VisualizationAgent
   extends Agent
   implements TreeModelListener
   {
   private static final String          className             = VisualizationAgent.class.getName();
   private        final JFrame          frame                 = new JFrame("Visualization");
   private        final MessageTemplate messageTemplate       = MessageTemplate.MatchOntology(EnvironmentalZoneAgent.occupancyOntology);
   private              int             previousHour          = 0;
   private              SimulationModel simulationModel       = null;
   private        final EnergyConsumer  sustainabilityBase    = new EnergyConsumer(null, "Sustainability Base", null);
   private static final String          visualizationOntology = className + ":Visualization";

   public static void main(String[] args)
      {
      new VisualizationAgent().setup();
      }

   protected void setup()
      {
      logger.trace("Begin setup");
      super.setup();

      EnergyConsumer building    = new EnergyConsumer("mcsmith-mcsmith-NorthBuildingAgent", "North Building", sustainabilityBase);
      EnergyConsumer heatingZone = new EnergyConsumer("mcsmith-mcsmith-NorthBuildingHeatingZoneAgent", "Heating Zone", building);
      EnergyConsumer coolingZone = new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0CoolingZoneAgent", "Floor 0 Cooling Zone", heatingZone);

      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor0WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", coolingZone));
      heatingZone.childList.add(coolingZone);
      coolingZone = new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1CoolingZoneAgent", "Floor 1 Cooling Zone", heatingZone);
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-NorthBuildingFloor1WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", coolingZone));
      heatingZone.childList.add(coolingZone);
      building.childList.add(heatingZone);
      sustainabilityBase.childList.add(building);
      building = new EnergyConsumer("mcsmith-mcsmith-SouthBuilding", "South Building", sustainabilityBase);
      heatingZone = new EnergyConsumer("mcsmith-mcsmith-SouthBuildingHeatingZone", "Heating Zone", building);
      coolingZone = new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0CoolingZoneAgent", "Floor 0 Cooling Zone", heatingZone);
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom4LightingZoneAgent", "Meeting Room 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom5LightingZoneAgent", "Meeting Room 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0MeetingRoom6LightingZoneAgent", "Meeting Room 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor0WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", coolingZone));
      heatingZone.childList.add(coolingZone);
      coolingZone = new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1CoolingZoneAgent", "Floor 1 Cooling Zone", heatingZone);
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom0LightingZoneAgent", "Meeting Room 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom1LightingZoneAgent", "Meeting Room 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom2LightingZoneAgent", "Meeting Room 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1MeetingRoom3LightingZoneAgent", "Meeting Room 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace0LightingZoneAgent", "Work Space 0 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace1LightingZoneAgent", "Work Space 1 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace2LightingZoneAgent", "Work Space 2 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace3LightingZoneAgent", "Work Space 3 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace4LightingZoneAgent", "Work Space 4 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace5LightingZoneAgent", "Work Space 5 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace6LightingZoneAgent", "Work Space 6 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace7LightingZoneAgent", "Work Space 7 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace8LightingZoneAgent", "Work Space 8 Lighting Zone", coolingZone));
      coolingZone.childList.add(new EnergyConsumer("mcsmith-mcsmith-SouthBuildingFloor1WorkSpace9LightingZoneAgent", "Work Space 9 Lighting Zone", coolingZone));
      heatingZone.childList.add(coolingZone);
      building.childList.add(heatingZone);
      sustainabilityBase.childList.add(building);
      simulationModel = new SimulationModel(sustainabilityBase);
      simulationModel.addTreeModelListener(this);

      JTreeTable treeTable = new JTreeTable(simulationModel);

      frame.addWindowListener(new WindowAdapter()
         {
         public void windowClosing(final WindowEvent windowEvent)
            {
            System.exit(0);
            }
         });
      frame.getContentPane().add(new JScrollPane(treeTable));
      frame.pack();
      frame.setVisible(true);
      addBehaviour(new MonitorActivityBehaviour(this));
      logger.trace("End   setup");
      }

   @Override
   public void treeNodesChanged(final TreeModelEvent treeModelEvent)
      {
   // SwingUtilities.invokeLater(new Runnable()
   //    {
   //    public void run()
            {
            frame.repaint();
            }
   //    });
      }

   @Override
   public void treeNodesInserted(final TreeModelEvent treeModelEvent)
      {
      throw new IllegalStateException("Not implemented");
      }

   @Override
   public void treeNodesRemoved(final TreeModelEvent treeModelEvent)
      {
      throw new IllegalStateException("Not implemented");
      }

   @Override
   public void treeStructureChanged(final TreeModelEvent treeModelEvent)
      {
      throw new IllegalStateException("Not implemented");
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
            final EnergyConsumer energyConsumer = EnergyConsumer.agentNameMap.get(inboundMessage.getSender().getLocalName());

            if (energyConsumer == null)
               {
               logger.error("Unable to locate the energy consumer related to " + inboundMessage.getSender().getLocalName());
               }
            else
               {
               try
                  {
                  final WorkSpaceAgent.OccupancyChange occupancyChange = (WorkSpaceAgent.OccupancyChange) inboundMessage.getContentObject();
                  final int                            hour            = occupancyChange.simulatedDateTime.getHours();
                  final int                            column          = hour * 2 + 2;

                  if (hour > previousHour)
                     {
                     simulationModel.initializeHour(energyConsumer, hour);
                     previousHour = hour;
                     }
                  logger.debug("Received " + energyConsumer + " occupancy change:  " + occupancyChange.deltaOccupancy + " for " + occupancyChange.simulatedDateTime);
                  logger.debug("Before:  Occupancy of " + energyConsumer + " is " + (Integer) simulationModel.getValueAt(energyConsumer, column));
                  simulationModel.setValueAt(((Integer) simulationModel.getValueAt(energyConsumer, column)) + occupancyChange.deltaOccupancy,
                                             energyConsumer,
                                             column);
                  logger.debug("After:   Occupancy of " + energyConsumer + " is " + (Integer) simulationModel.getValueAt(energyConsumer, column));
                  }
               catch (final UnreadableException unreadableException)
                  {
                  logger.error("Unable to get occupancy change object:  " + unreadableException.getMessage(),
                               unreadableException);
                  }
               }
            }
         logger.trace("End   action");
         }
      }
   }
