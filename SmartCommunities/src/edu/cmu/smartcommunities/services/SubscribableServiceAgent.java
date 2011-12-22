package edu.cmu.smartcommunities.services;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.Hashtable;
import java.util.Map;

public class SubscribableServiceAgent
   extends ServiceAgent
   {
   private   static final String           ONTOLOGY              = "edu.cmu.smartcommunities.jade.core.SubscribableServiceAgent";
   public    static final String           SUBSCRIBE             = "(subscribe)";
   public    static final String           SUBSCRIPTION_ONTOLOGY = ONTOLOGY + ":Subscription";
   public    static final String           UNSUBSCRIBE           = "(unsubscribe)";
   private   static final long             serialVersionUID      = 5253950525670332332L;
   protected        final Map<String, AID> subscriberMap         = new Hashtable<String, AID>();

   public Map<String, AID> getSubscriberMap()
      {
      return subscriberMap;
      }

   protected void setup()
      {
      super.setup();
      addBehaviour(new ManageSubscribersBehaviour(this));
      }
   }
