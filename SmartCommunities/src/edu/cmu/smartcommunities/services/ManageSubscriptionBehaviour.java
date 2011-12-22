package edu.cmu.smartcommunities.services;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ManageSubscriptionBehaviour
   extends OneShotBehaviour
   {
   private static final MessageTemplate messageTemplate     = MessageTemplate.MatchOntology(SubscribableServiceAgent.SUBSCRIPTION_ONTOLOGY);
   private static final long            serialVersionUID    = 8998831609143529778L;
   private        final String          serviceName;
   private        final String          serviceType;
   private        final Agent           subscriberAgent;
   private        final String          subscriptionRequest;

   public ManageSubscriptionBehaviour(final String serviceName,
                                      final String serviceType,
                                      final Agent  subscriberAgent,
                                      final String subscriptionRequest)
      {
      super(subscriberAgent,
            null);
      if (serviceType == null || subscriberAgent == null || subscriptionRequest == null)
         {
         throw new IllegalArgumentException();
         }
      this.serviceName = serviceName;
      this.serviceType = serviceType;
      this.subscriberAgent = subscriberAgent;
      this.subscriptionRequest = subscriptionRequest;
      }

   @Override
   public void action()
      {
      final DFAgentDescription[] subscribeeAgent = ServiceAgent.search(subscriberAgent,
                                                                       serviceName,
                                                                       serviceType);

      if (subscribeeAgent != null && subscribeeAgent.length > 0)
         {
         for (int i = 0; i < subscribeeAgent.length; i++)
            {
            final ACLMessage requestMessage = new ACLMessage(ACLMessage.REQUEST);

            requestMessage.addReceiver(subscribeeAgent[i].getName());
            requestMessage.setOntology(SubscribableServiceAgent.SUBSCRIPTION_ONTOLOGY);
            requestMessage.setContent(subscriptionRequest);
            subscriberAgent.send(requestMessage);

            final ACLMessage responseMessage = subscriberAgent.blockingReceive(messageTemplate);

            logger.debug("Received registration response of " +
                         responseMessage.getPerformative() +
                         " from " +
                         responseMessage.getSender().getName());
            }
         }
      else
         {
         if (subscriptionRequest.equals(SubscribableServiceAgent.SUBSCRIBE))
            {
            final int sleepInterval = 10000;

            try
               {
               logger.warn("Found no service " +
                           (serviceName == null ? "" : "named " + serviceName + " ") +
                           "of type " +
                           serviceType +
                           ".  Resubmitting subscription request in " +
                           (sleepInterval / 1000) +
                           " seconds.");
               Thread.sleep(sleepInterval);
               }
            catch (final InterruptedException interruptedException)
               {
               }
            finally
               {
               subscriberAgent.addBehaviour(this);
               }
            }
         }
      }
   }
