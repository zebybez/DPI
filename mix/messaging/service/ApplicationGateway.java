package messaging.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ApplicationGateway<IN extends Serializable, OUT extends Serializable> {

    private Map<String, OUT> sendItemsMap;
    private Map<IN, String> receivedItemsIdMap;
    private Map<IN, String> receivedItemsCorrelationMap;
    private MessageService messageService;
    private Message message;
    private OUT sendingObj;

    public ApplicationGateway(Destinations outgoing, Destinations incoming) {
        sendItemsMap = new HashMap<>();
        receivedItemsIdMap = new HashMap<>();
        receivedItemsCorrelationMap = new HashMap<>();
        messageService = new MessageService(outgoing, incoming, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                parseMessage(getObjectFromMsg(message));
            }
        });
    }

    /***
     * creates a message to later be sent
     * @param object the object to put in the message
     */
    public void createMessage(OUT object) {
        try {
            sendingObj = object;
            message = messageService.getSession().createObjectMessage(object);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /***
     * sends the message
     */
    public void sendMessage() {
        try {
            sendItemsMap.put(message.getJMSMessageID(), sendingObj);
            messageService.sendMessage(message);
        } catch (NullPointerException e) {
            System.out.println("instantiate the message first using the \"createMessage\" Method");
            e.printStackTrace();
        } catch (JMSException e){
            e.printStackTrace();
        }
    }

    /***
     * sets the correlation id for the next message sent
     * @param id the id to set
     */
    public void setCorrelationId(String id) {
        try {
            message.setJMSCorrelationID(id);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("instantiate the message first using the \"createMessage\" Method");
            e.printStackTrace();
        }
    }

    /***
     * gets the message id of the last message created
     * @return the id of the message, or and empty string if it failed
     */
    public String getMessageId() {
        try {
            return message.getJMSMessageID();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("instantiate the message first using the \"createMessage\" Method");
            e.printStackTrace();
        }
        return "";
    }

    public String getCorrelationIdByReceivedObject(IN object){
        return receivedItemsCorrelationMap.get(object);
    }

    public String getMessageIdByReceivedObject(IN object){
        return receivedItemsIdMap.get(object);
    }

    public OUT getSendObjectByMessageId(String id){
        return sendItemsMap.get(id);
    }

    private IN getObjectFromMsg(Message message) {
        ObjectMessage objMsg = (ObjectMessage) message;
        try {
            IN object = (IN) objMsg.getObject();
            receivedItemsCorrelationMap.put(object, message.getJMSCorrelationID());
            receivedItemsIdMap.put(object, message.getJMSMessageID());
            return object;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * exposes the object gotten from an incoming message to the parent class;
     * @param object the object in the message
     */
    public void parseMessage(IN object) {
        throw new IllegalStateException("this method should be overridden");
    }
}
