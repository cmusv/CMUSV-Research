package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;

public class ReceiverAgent
   extends Agent
   {
   protected void setup()
      {
      super.setup();
      addBehaviour(new ReceiveMessageBehaviour(this));
      }
   }
