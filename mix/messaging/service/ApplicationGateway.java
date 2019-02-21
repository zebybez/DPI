package messaging.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

//todo make special serializer instead of object messages.
public abstract class ApplicationGateway<IN extends Serializable, OUT extends Serializable> {

    private MessageGateway messageGateway;
    private Message message;

    public ApplicationGateway(String outgoing, String incoming) {
        messageGateway = new MessageGateway(outgoing, incoming, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    parseMessage(getObjectFromMsg(message), message.getJMSCorrelationID());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setAggregationId(String id){
        try {
            message.setStringProperty("aggregation", id);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setOutgoingQueue(String string){
        messageGateway.setOutgoingQueue(string);
    }

    public void setIncomingQueue(String string){
        messageGateway.setIncomingQueue(string);
    }

    /***
     * creates a message to later be sent
     * @param object the object to put in the message
     */
    public void createMessage(OUT object) {
        try {
            message = messageGateway.getSession().createObjectMessage(object);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /***
     * sends the message
     */
    public void sendMessage() {
        try {
            messageGateway.sendMessage(message);
        } catch (NullPointerException e) {
            System.out.println("instantiate the message first using the \"createMessage\" Method");
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

    private IN getObjectFromMsg(Message message) {
        ObjectMessage objMsg = (ObjectMessage) message;
        try {
            IN object = (IN) objMsg.getObject();
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
    public abstract void parseMessage(IN object, String correlationId);
}
