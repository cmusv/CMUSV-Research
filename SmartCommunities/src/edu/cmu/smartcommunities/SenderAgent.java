package edu.cmu.smartcommunities;

import edu.cmu.smartcommunities.jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SenderAgent
   extends Agent
   {
   private static final long serialVersionUID = 3746835584309689550L;

   protected void setup()
      {
      super.setup();
      addBehaviour(new SendMessageBehaviour(this));
      }

   public class SendMessageBehaviour
      extends OneShotBehaviour
      {
      private static final String       content               = "This space is intentionally left blank.";
      private        final long         initialDelayTime;
      private static final String       language              = "English";
      private        final int          messageCount;
      private static final String       ontology              = "SmartCommunities";
      private        final ACLMessage[] outboundMessage;
      private static final String       performative          = "INFORM";
      private        final long[]       preSendDelayTime;
      private static final String       propertyNamePreamble  = "SendMessageBehaviour.Message.";
      private        final int          repeatCount;
      private static final String       receiverLocalName     = "mcsmith-mcsmith-ReceiverAgent";
      private static final long         serialVersionUID      = -7942170919509482095L;

      public SendMessageBehaviour(final Agent agent)
         {
         super(agent);

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
         outboundMessage = new ACLMessage[messageCount];
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
                  outboundMessage[j].setContent(content);
                  send(outboundMessage[j]);
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
         outboundMessage[messageNumber] = new ACLMessage(performative.equals("ACCEPT_PROPOSAL")  ? ACLMessage.ACCEPT_PROPOSAL  :
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
         outboundMessage[messageNumber].addReceiver(receiverAgentId);
         outboundMessage[messageNumber].setContent(content);
         outboundMessage[messageNumber].setLanguage(language);
         outboundMessage[messageNumber].setOntology(ontology);
         outboundMessage[messageNumber].setSender(getAID());
         }
      }
   }
