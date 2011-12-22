package edu.cmu.smartcommunities.services;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ServiceAgent
   extends Agent
   {
   private          final DFAgentDescription agentDescription = new DFAgentDescription();
   private   static final long               serialVersionUID = 3398813868856424334L;
   protected              String             serviceName      = singleton;
   protected              String             serviceType      = null;
   private   static final String             singleton        = "Singleton";

   private void advertise()
      {
      if (serviceName == null)
         {
         throw new IllegalStateException("Cannot setup a service agent without having previously defined its service type");
         }
      try
         {
         agentDescription.addServices(getServiceDescription(serviceName,
                                                            serviceType));
         agentDescription.setName(new AID(getName(),
                                          AID.ISGUID));
         DFService.register(this,
                            agentDescription);
         }
      catch (final FIPAException fipaException)
         {
         agentDescription.setName(null);
         throw new RuntimeException(fipaException);
         }
      }

   private void deadvertise()
      {
      try
         {
         DFService.deregister(this, agentDescription);
         }
      catch (final FIPAException fipaException)
         {
         throw new RuntimeException(fipaException);
         }
      }

   private static ServiceDescription getServiceDescription(final String name,
                                                           final String type)
      {
      final ServiceDescription serviceDescription = new ServiceDescription();

      if (name != null)
         {
         serviceDescription.setName(name);
         }
      serviceDescription.setType(type);
      return serviceDescription;
      }

   public static DFAgentDescription[] search(final Agent  agent,
                                             final String type)
      {
      return search(agent,
                    null,
                    type);
      }

   public static DFAgentDescription[] search(final Agent  agent,
                                             final String name,
                                             final String type)
      {
      DFAgentDescription[] results = null;

      try
         {
         final DFAgentDescription agentDescription     = new DFAgentDescription();
         final ServiceDescription namedServiceProvider = getServiceDescription(name,
                                                                               type);

         agentDescription.addServices(namedServiceProvider);
         results = DFService.search(agent,
                                    agentDescription);
         if (results.length == 0)
            {
            final ServiceDescription genericServiceProvider = getServiceDescription(null,
                                                                                    type);

            agentDescription.removeServices(namedServiceProvider);
            agentDescription.addServices(genericServiceProvider);
            results = DFService.search(agent,
                                       agentDescription);
            }
         }
      catch (final FIPAException fipaException)
         {
         fipaException.printStackTrace();
         }
      return results;
      }

   @Override
   protected void setup()
      {
      super.setup();
      advertise();
      }

   @Override
   protected void takeDown()
      {
      deadvertise();
      super.takeDown();
      }
   }
