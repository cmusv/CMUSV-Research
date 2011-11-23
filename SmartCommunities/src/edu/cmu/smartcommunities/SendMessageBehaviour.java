package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;
import edu.cmu.smartcommunities.jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;

public class SendMessageBehaviour
   extends OneShotBehaviour
   {
   private final static String       content               = "This space is intentionally left blank.";
   private final        long         initialDelayTime;
   private final static String       language              = "English";
   private final        ACLMessage[] message;
   private final        int          messageCount;
   private final static String       ontology              = "SmartCommunities";
   private final static String       performative          = "INFORM";
   private final        long[]       preSendDelayTime;
   private final static String       propertyNamePreamble  = "SendMessageBehaviour.Message.";
   private final        int          repeatCount;
   private final static String       receiverLocalName     = "ReceiverAgent";

   public SendMessageBehaviour(final Agent              agent,
                               final ExtendedProperties extendedProperties)
      {
      super(agent, extendedProperties);

      long initialDelayTime = 0;

      try
         {
         initialDelayTime = extendedProperties.getIntProperty("SendMessageBehaviour.InitialDelayTime", 1);
         logger.trace("initial delay time:  " + initialDelayTime);
         }
      catch (final NumberFormatException numberFormatException)
         {
         logger.warn("Unable to interpret initial delay time",
                     numberFormatException);
         }
      this.initialDelayTime = initialDelayTime;

      int messageCount = 1;

      try
         {
         messageCount = extendedProperties.getIntProperty("SendMessageBehaviour.MessageCount", 1);
         logger.trace("message count:  " + messageCount);
         }
      catch (final NumberFormatException numberFormatException)
         {
         logger.warn("Unable to interpret messageSequenceLength",
                     numberFormatException);
         }
      this.messageCount = messageCount;
      message = new ACLMessage[messageCount];
      preSendDelayTime = new long[messageCount];
      for (int i = 0; i < messageCount; i++)
         {
         createMessage(i);
         }

      int repeatCount = 1;

      try
         {
         repeatCount = extendedProperties.getIntProperty("SendMessageBehaviour.RepeatCount", 1);
         logger.trace("repeat count:  " + repeatCount);
         }
      catch (final NumberFormatException numberFormatException)
         {
         logger.warn("Unable to interpret repeat count",
                     numberFormatException);
         }
      this.repeatCount = repeatCount;
      }

   /**
    * 
    */

   @Override
   public void action()
      {
      logger.trace("Begin action");
      try
         {
         Thread.sleep(initialDelayTime);
         for (int i = 0; i < repeatCount; i++)
            {
            for (int j = 0; j < messageCount; j++)
               {
               Thread.sleep(preSendDelayTime[j]);
               myAgent.send(message[j]);
               }
            }
         }
      catch (final InterruptedException interruptedException)
         {
         // Probably being awakened to shut down.
         }
      logger.trace("End   action");
      }

   /**
    * Creates a message that will be published.  The makeup of each message is determined by
    * the contents of the agent's property file.  Properties of interest are:
    *
    * <list>
    *    <li><code>PublishMessageBehaviour.Message.<em>messageNumber</em>.<em>Content</em></li>
    *    <li><code>PublishMessageBehaviour.Message.<em>messageNumber</em>.<em>Language</em></li>
    *    <li><code>PublishMessageBehaviour.Message.<em>messageNumber</em>.<em>Ontology</em></li>
    *    <li><code>PublishMessageBehaviour.Message.<em>messageNumber</em>.<em>Performative</em></li>
    *    <li><code>PublishMessageBehaviour.Message.<em>messageNumber</em>.<em>PreSendDelayTime</em></li>
    * </list>
    * 
    * @param messageNumber
    */

   private void createMessage(final int messageNumber)
      {
      final String propertyNamePreamble = new StringBuffer(SendMessageBehaviour.propertyNamePreamble).append(messageNumber).append(".").toString();
      final String content              = extendedProperties.getProperty(new StringBuffer(propertyNamePreamble).append("Content").toString(), SendMessageBehaviour.content);
      final String language             = extendedProperties.getProperty(new StringBuffer(propertyNamePreamble).append("Language").toString(), SendMessageBehaviour.language);
      final String ontology             = extendedProperties.getProperty(new StringBuffer(propertyNamePreamble).append("Ontology").toString(), SendMessageBehaviour.ontology);
      final String performative         = extendedProperties.getProperty(new StringBuffer(propertyNamePreamble).append("Performative").toString(), SendMessageBehaviour.performative).toUpperCase();
      final AID    receiverAgentId      = new AID();

      try
         {
         preSendDelayTime[messageNumber] = extendedProperties.getIntProperty(new StringBuffer(propertyNamePreamble).append("PreSendDelayTime").toString(), 1000);
         }
      catch (final NumberFormatException numberFormatException)
         {
         preSendDelayTime[messageNumber] = 1000;
         logger.warn("Unable to interpret pre-send delay time for message " + messageNumber,
                     numberFormatException);
         }
      message[messageNumber] = new ACLMessage(performative.equals("ACCEPT_PROPOSAL")  ? ACLMessage.ACCEPT_PROPOSAL  :
                                              performative.equals("AGREE")            ? ACLMessage.AGREE            :
                                              performative.equals("CANCEL")           ? ACLMessage.CANCEL           :
                                              performative.equals("CFP")              ? ACLMessage.CFP              :
                                              performative.equals("CONFIRM")          ? ACLMessage.CONFIRM          :
                                              performative.equals("DISCONFIRM")       ? ACLMessage.DISCONFIRM       :
                                              performative.equals("FAILURE")          ? ACLMessage.FAILURE          :
                                              performative.equals("INFORM")           ? ACLMessage.INFORM           :
                                              performative.equals("INFORM_IF")        ? ACLMessage.INFORM_IF        :
                                              performative.equals("INFORM_REF")       ? ACLMessage.INFORM_REF       :
                                              performative.equals("NOT_UNDERSTOOD")   ? ACLMessage.NOT_UNDERSTOOD   :
                                              performative.equals("PROPAGATE")        ? ACLMessage.PROPAGATE        :
                                              performative.equals("PROPOSE")          ? ACLMessage.PROPOSE          :
                                              performative.equals("PROXY")            ? ACLMessage.PROXY            :
                                              performative.equals("QUERY_IF")         ? ACLMessage.QUERY_IF         :
                                              performative.equals("QUERY_REF")        ? ACLMessage.QUERY_REF        :
                                              performative.equals("REFUSE")           ? ACLMessage.REFUSE           :
                                              performative.equals("REJECT_PROPOSAL")  ? ACLMessage.REJECT_PROPOSAL  :
                                              performative.equals("REQUEST")          ? ACLMessage.REQUEST          :
                                              performative.equals("REQUEST_WHEN")     ? ACLMessage.REQUEST_WHEN     :
                                              performative.equals("REQUEST_WHENEVER") ? ACLMessage.REQUEST_WHENEVER :
                                              performative.equals("SUBSCRIBE")        ? ACLMessage.SUBSCRIBE        :
                                                                                        ACLMessage.UNKNOWN);
      receiverAgentId.setLocalName(extendedProperties.getProperty(new StringBuffer(propertyNamePreamble).append("ReceiverLocalName").toString(), SendMessageBehaviour.receiverLocalName));
      message[messageNumber].addReceiver(receiverAgentId);
      message[messageNumber].setContent(content);
      message[messageNumber].setLanguage(language);
      message[messageNumber].setOntology(ontology);
      message[messageNumber].setSender(myAgent.getAID());
      }
   }
