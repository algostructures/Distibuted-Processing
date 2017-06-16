import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Client implements Work{
    

    public static void main(String[] args)throws Exception {        
        
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        String text=InetAddress.getLocalHost().getHostAddress()+" "+args[1];
        TextMessage message = session.createTextMessage(text);
        producer.send(message); 
        producer.close();
        session.close();
        connection.close();   
        
        Client obj = new Client();
        Work stub = (Work)UnicastRemoteObject.exportObject(obj,0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(args[1],stub);
        
        
        
    }

    @Override
    public Object doWork(Task t)  {
        Object o=null;
        try{
        o=t.doWork();   
        System.out.println((String)o);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return o;
        
    }

}
