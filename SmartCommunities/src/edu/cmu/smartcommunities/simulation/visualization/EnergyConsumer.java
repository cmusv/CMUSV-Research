package edu.cmu.smartcommunities.simulation.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyConsumer
   {
   public  static final Map<String, EnergyConsumer> agentNameMap    = new HashMap<String, EnergyConsumer>();
   public         final List<EnergyConsumer>        childList       = new ArrayList<EnergyConsumer>();
   public         final String                      displayName;
                  final int[]                       energyConsumed  = new int[hoursPerDay];
                  final boolean[]                   hourInitialized = new boolean[hoursPerDay];
           static final int                         hoursPerDay     = 24; 
                  final int[]                       occupancy       = new int[hoursPerDay];
   public         final EnergyConsumer              parent;

   public EnergyConsumer(final String         agentLocalName,
                         final String         displayName,
                         final EnergyConsumer parent)
      {
      agentNameMap.put(agentLocalName, this);
      this.displayName = displayName;
      for (int hour = hoursPerDay - 1; hour >= 0; hour--)
         {
         energyConsumed[hour] = 0;
         hourInitialized[hour] = hour == 0;
         occupancy[hour] = 0;
         }
      this.parent = parent;
      }

   public String toString()
      {
      return displayName;
      }
   }
