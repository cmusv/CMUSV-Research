package edu.cmu.smartcommunities.simulation.visualization;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class SimulationModel
   extends AbstractTreeTableModel
   {
   public SimulationModel(final EnergyConsumer energyConsumer)
      { 
	   super(energyConsumer);
      }

   //
   // The TreeModel interface
   //

   public Object getChild(Object node,
                          int i)
      {
      return ((EnergyConsumer) node).childList.get(i); 
      }

   public int getChildCount(Object node)
      {
      return ((EnergyConsumer) node).childList.size();
      }

   public boolean isLeaf(Object node)
      {
      return getChildCount(node) == 0;
      }

   //
   // The TreeTableNode interface. 
   //

   public Class getColumnClass(int column)
      {
      return column == 0 ? TreeTableModel.class : Integer.class;
      }

   public int getColumnCount()
      {
	   return 24 * 2 + 1;
      }

   public String getColumnName(int column)
      {
	   return column == 0 ? "Location" : column % 2 == 0 ? "O" : "E";
	   }

   private int getHour(final int column)
      {
      return (column - 1) / 2;
      }

   public Object getValueAt(Object node, int column)
      {
      final EnergyConsumer energyConsumer = (EnergyConsumer) node;
      final int            hour           = getHour(column);

      return column     == 0 ? energyConsumer.displayName :
             column % 2 == 0 ? energyConsumer.occupancy[hour] :
                               energyConsumer.energyConsumed[hour];
      }

   public void initializeHour(      EnergyConsumer energyConsumer,
                              final int            hour)
      {
      final Queue<EnergyConsumer> energyConsumerQueue = new ArrayDeque<EnergyConsumer>();

      while (energyConsumer.parent != null)
         {
         energyConsumer = energyConsumer.parent;
         }
      energyConsumerQueue.add(energyConsumer);
      while ((energyConsumer = energyConsumerQueue.poll()) != null)
         {
         energyConsumer.energyConsumed[hour] = 0;
         energyConsumer.occupancy[hour] = energyConsumer.occupancy[(hour + 24 - 1) % 24];
         energyConsumerQueue.addAll(energyConsumer.childList);
         }
      }

   public void setValueAt(Object value,
                          Object node,
                          int    column)
      {
      if (column != 0) // Location name is immutable
         {
               EnergyConsumer energyConsumer = (EnergyConsumer) node;
         final int            hour           = getHour(column);

         if (column % 2 == 0)
            {
            energyConsumer.occupancy[hour] = (Integer) value;
            }
         else
            {
            energyConsumer.energyConsumed[hour] = (Integer) value;
            }

         final Stack<EnergyConsumer> energyConsumerStack = new Stack<EnergyConsumer>();

         energyConsumerStack.push(energyConsumer);
         while (energyConsumer.parent != null) 
            {
            energyConsumer = energyConsumer.parent;
            energyConsumerStack.push(energyConsumer);
            }
         fireTreeNodesChanged(energyConsumer,
                              energyConsumerStack.toArray(),
                              new int[]{},
                              energyConsumer.childList.toArray());
         }
      }
   }
