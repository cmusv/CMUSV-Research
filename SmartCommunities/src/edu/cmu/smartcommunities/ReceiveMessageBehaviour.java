package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveMessageBehaviour
   extends CyclicBehaviour
   {
   public ReceiveMessageBehaviour(final Agent agent)
      {
      super(agent);
      }

   /**
    *   Receive inbound messages.
    */

   @Override
   public void action()
      {
      logger.trace("Begin action");

      final ACLMessage message = myAgent.receive();

      if (message == null)
         {
         block();
         }
      else
         {
         logger.info(message.getContent());
         }
      logger.trace("End   action");
      }
   }
