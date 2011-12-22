package edu.cmu.smartcommunities.simulation;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.OneShotBehaviour;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class CreateAgentsBehaviour
   extends OneShotBehaviour
   {
   public CreateAgentsBehaviour(final Agent agent,
                                final ExtendedProperties extendedProperties)
      {
      super(agent,
            extendedProperties);
      }

   @Override
   public void action()
      {
      final int agentTypes = extendedProperties.getIntProperty("AgentTypes", 0);
      final AgentContainer agentContainer = myAgent.getContainerController();

      for (int agentType = 0; agentType < agentTypes; agentType++)
         {
         final String agentTypePrefix = "AgentType." + agentType + ".";
         final String className       = extendedProperties.getProperty(agentTypePrefix + "ClassName", null);

         if (className == null)
            {
            logger.warn("Unable to determine class name for agent type " + agentType);
            }
         else
            {
            final int subTypes = extendedProperties.getIntProperty(agentTypePrefix + "SubTypes", 0);

            if (subTypes == 0)
               {
               logger.warn("Unable to determine the number of subtypes of agent type " + agentType);
               }
            else
               {
               for (int subType = 0; subType < subTypes; subType++)
                  {
                  final String subTypePrefix    = agentTypePrefix + "SubType." + subType + ".";
                  final int    agentCount       = extendedProperties.getIntProperty(subTypePrefix + "AgentCount", 0);
                  final String localNamePrefix  = extendedProperties.getProperty(subTypePrefix + "LocalNamePrefix", null);
                  final String propertyFileName = extendedProperties.getProperty(subTypePrefix + "PropertyFileName", null);

                  for (int agent = 0; agent < agentCount; agent++)
                     {
                     try
                        {
                        agentContainer.createNewAgent(localNamePrefix + agent,
                                                      className,
                                                      new Object[] { "import = " + propertyFileName } );
                        }
                     catch (final StaleProxyException staleProxyException)
                        {
                        logger.error("Unable to create agent " + localNamePrefix + agent,
                                     staleProxyException);
                        }
                     }
                  }
               }
            }
         }
      }
   }
