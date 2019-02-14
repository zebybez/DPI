package messaging.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ApplicationGateway<IN extends Serializable, OUT extends Serializable> {

//    private Map<String, OUT> sendItemsMap;
//    private Map<String, String> receivedItemsMap;
    MessageService messageService;

    public ApplicationGateway(Destinations outgoing, Destinations incomming){
       // sendItemsMap = new HashMap<>();
        messageService = new MessageService(outgoing, incomming, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                parseMessage(getObjectFromMsg(message));
            }
        });


    }

    public String sendMessage(OUT msg){
        try {
            Message message = messageService.getSession().createObjectMessage(msg);

          //  sendItemsMap.put(message.getJMSMessageID(), msg);
            messageService.sendMessage(message);
            return message.getJMSMessageID();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public IN getObjectFromMsg(Message message){
//        try {
//            receivedItemsMap.put(message.getJMSMessageID(), message.getJMSCorrelationID());
//            if(sendItemsMap.containsKey(message.getJMSCorrelationID())){
//                sendMessage();
//            }
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
        ObjectMessage objMsg = (ObjectMessage) message;
        try{
            IN object = (IN) objMsg.getObject();
            //object.setCorrelationId();
        } catch (JMSException e){
            e.printStackTrace();
        }
        return null;
    }

    public void parseMessage(IN message){

    }
}
