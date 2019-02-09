package messaging.service;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;

public class MessageService {

    Connection connection; // to connect to the ActiveMQ
    Session session; // session for creating messages, producers and

    Destination destination; // reference to a queue/topic destination
    Destination recieveDestination; //reference to a queue/topic

    MessageProducer producer; // for sending messages
    MessageConsumer consumer; // for receiving messages

    MessageListener listener; // listens for messages

    public MessageService(Destinations outgoing, Destinations incoming, MessageListener listener) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(outgoing.toString());
            recieveDestination = session.createQueue(incoming.toString());

            producer = session.createProducer(destination);
            consumer = session.createConsumer(recieveDestination);
            consumer.setMessageListener(listener);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public boolean sendMessage(Serializable objMsg) {
        try {
            Message msg = session.createObjectMessage(objMsg);
            producer.send(msg);
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

    public boolean startConnection() {
        return false;
    }

    public boolean stopConnection() {
        return false;
    }


}
