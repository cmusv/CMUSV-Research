package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.services.SubscribableServiceAgent;
import edu.cmu.smartcommunities.services.ManageSubscriptionBehaviour;
import edu.cmu.smartcommunities.simulation.TimeAgent;

public class PersonAgent
   extends Agent
   {
   protected void setup()
      {
      super.setup();
      /*
      addBehaviour(new ManageSubscriptionBehaviour(null,
                                             TimeAgent.SERVICE_TYPE,
                                             this,
                                             SubscribableServiceAgent.SUBSCRIBE));
      */
      addBehaviour(new WorkBehaviour(this, extendedProperties));
      }

   protected void takeDown()
      {
      /*
      addBehaviour(new ManageSubscriptionBehaviour(null,
                                             TimeAgent.SERVICE_TYPE,
                                             this,
                                             SubscribableServiceAgent.UNSUBSCRIBE));
      */
      super.takeDown();
      }
   }
