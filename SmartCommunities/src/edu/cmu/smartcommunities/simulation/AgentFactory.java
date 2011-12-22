package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;

public class AgentFactory
   extends Agent
   {
   protected void setup()
      {
      super.setup();
      addBehaviour(new CreateAgentsBehaviour(this, extendedProperties));
      }
   }
