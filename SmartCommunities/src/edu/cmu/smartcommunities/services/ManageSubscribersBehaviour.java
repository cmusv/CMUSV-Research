package edu.cmu.smartcommunities.services;

import edu.cmu.smartcommunities.jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Map;

public class ManageSubscribersBehaviour
   extends CyclicBehaviour
   {
   private static final MessageTemplate  messageTemplate  = MessageTemplate.and(MessageTemplate.MatchOntology(SubscribableServiceAgent.SUBSCRIPTION_ONTOLOGY),
                                                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
   private static final long             serialVersionUID = 7983448018883840778L;
   private        final Map<String, AID> subscriberMap;

   public ManageSubscribersBehaviour(final SubscribableServiceAgent subscribableServiceAgent)
      {
      super(subscribableServiceAgent);
      subscriberMap = subscribableServiceAgent.getSubscriberMap();
      }

   @Override
   public void action()
      {
      final ACLMessage requestMessage = myAgent.receive(messageTemplate);

      if (requestMessage == null)
         {
         block();
         }
      else
         {
         final String     content         = requestMessage.getContent();
         final ACLMessage responseMessage = requestMessage.createReply();
         final AID        senderAgent     = requestMessage.getSender();

         synchronized (subscriberMap)
            {
            if (content.equals(SubscribableServiceAgent.SUBSCRIBE))
               {
               responseMessage.setPerformative(subscriberMap.put(senderAgent.getName(),
                                                                 senderAgent) == null ? ACLMessage.CONFIRM : ACLMessage.DISCONFIRM);
               }
            else
               {
               if (content.equals(SubscribableServiceAgent.UNSUBSCRIBE))
                  {
                  responseMessage.setPerformative(subscriberMap.remove(senderAgent.getName()) == null ? ACLMessage.DISCONFIRM : ACLMessage.CONFIRM);
                  }
               else
                  {
                  responseMessage.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                  }
               }
            }
         myAgent.send(responseMessage);
         }
      }
   }
