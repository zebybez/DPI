package messaging.service;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;
//todo split up in sending and receiving parts
public class MessageGateway {

    Connection connection; // to connect to the ActiveMQ
    Session session; // session for creating messages, producers and

    Destination destination; // reference to a queue/topic destination
    Destination recieveDestination; //reference to a queue/topic

    MessageProducer producer; // for sending messages
    MessageConsumer consumer; // for receiving messages

    MessageListener listener; // listens for messages

    public MessageGateway(String outgoing, String incoming, MessageListener listener) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connectionFactory.setTrustAllPackages(true);
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            setOutgoingQueue(outgoing);
            setIncomingQueue(incoming);
            consumer.setMessageListener(listener);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public boolean sendMessage(Message objMsg) {
        try {

            String test = objMsg.getJMSMessageID();
            producer.send(objMsg);
            return true;
        } catch (JMSException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
        try {
            consumer.setMessageListener(listener);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void setOutgoingQueue(String queue) {

        try {
            destination = session.createQueue(queue);
            producer = session.createProducer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
    public void setIncomingQueue(String queue){
        try {
            recieveDestination = session.createQueue(queue);
            consumer = session.createConsumer(recieveDestination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
