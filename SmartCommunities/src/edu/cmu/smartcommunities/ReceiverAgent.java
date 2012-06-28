package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiverAgent
   extends Agent
   {
   private static final long serialVersionUID = -3755437439093115731L;

   protected void setup()
      {
      super.setup();
      addBehaviour(new ReceiveMessageBehaviour(this));
      }

   private class ReceiveMessageBehaviour
      extends CyclicBehaviour
      {
      private static final long serialVersionUID = -7078708754408258944L;

      public ReceiveMessageBehaviour(final Agent agent)
         {
         super(agent);
         }

      /**
       *   Receive inbound messages and take appropriate action.
       */

      @Override
      public void action()
         {
         logger.trace("Begin action");

         final ACLMessage inboundMessage = receive();

         if (inboundMessage == null)
            {
            block();
            }
         else
            {
            logger.debug(inboundMessage.getContent());
            }
         logger.trace("End   action");
         }
      }
   }
