package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;

public class SenderAgent
   extends Agent
   {
   protected void setup()
      {
      super.setup();
      addBehaviour(new SendMessageBehaviour(this, extendedProperties)); // .extractSubset(getLocalName())));
      }
   }
