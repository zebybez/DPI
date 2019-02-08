package messaging.service;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class MessageService {

    Connection connection; // to connect to the ActiveMQ
    Session session; // session for creating messages, producers and

    Destination destination; // reference to a queue/topic destination
    Destination recieveDestination; //reference to a queue/topic

    MessageProducer producer; // for sending messages
    MessageConsumer consumer; // for receiving messages

    public MessageService(Destinations outgoing, Destinations incomming, MessageListener listener) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try{
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(outgoing.toString());
            recieveDestination = session.createQueue(incomming.toString());


        } catch (JMSException e){
            e.printStackTrace();
        }


        outgoing.toString();
    }

    public boolean startConnection(){
        return false;
    }

    public boolean stopConnection(){
        return false;
    }


}
