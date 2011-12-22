package edu.cmu.smartcommunities.services;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import java.util.Map;

public class BroadcastMessageBehaviour
   extends OneShotBehaviour
   {
   private final String           content;
   private final String           ontology;
   private final int              performative;
   private final Map<String, AID> subscriberMap;

   public BroadcastMessageBehaviour(final Agent            agent,
                                    final String           content,
                                    final String           ontology,
                                    final int              performative,
                                    final Map<String, AID> subscriberMap)
      {
      super(agent,
            null);
      this.content = content;
      this.ontology = ontology;
      this.performative = performative;
      this.subscriberMap = subscriberMap;
      }

   @Override
   public void action()
      {
      final ACLMessage aclMessage = new ACLMessage(performative);

      logger.debug("The subscriber map now contains " + subscriberMap.size() + " entries");
      synchronized (subscriberMap)
         {
         for (AID agentId:  subscriberMap.values())
            {
            aclMessage.addReceiver(agentId);
            }
         }
      aclMessage.setContent(content);
      aclMessage.setOntology(ontology);
      myAgent.send(aclMessage);
      }
   }
