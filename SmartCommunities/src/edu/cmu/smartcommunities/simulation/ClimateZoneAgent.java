package edu.cmu.smartcommunities.simulation;

import java.util.Date;

public class ClimateZoneAgent
   extends EnvironmentalZoneAgent
   {
// @Override
   protected void processOccupancyChange(final Date occupancyChangeDateTime)
      {
      // Since heating and cooling are provided by radiant methods, which are characterized
      // as having high latency, do not react to occupancy changes for now.
      }

   protected void setup()
      {
      super.setup();
      // add MonitorTemperatureBehaviour
      // add ModifyTemperatureBehaviour
      }
   }
