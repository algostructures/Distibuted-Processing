import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Stack;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

@SuppressWarnings("unused")
public class Server implements Runnable{
    static Stack<Task> tasks;

    public static void main(String[] args) throws Exception{        
        tasks=new Stack<>();
        for(int i=0;i<5;i++){
            tasks.push(new ExampleWork(i));
        }
        while(!tasks.isEmpty()){
            Server s1=new Server();
            s1.run();
        }

    }

    @Override
    public void run() {
        try{            
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");      
            connectionFactory.setWatchTopicAdvisories(false);
            Connection connection = connectionFactory.createConnection();
            connection.start();        
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);        
            Destination destination = session.createQueue("TEST.FOO");        
            MessageConsumer consumer = session.createConsumer(destination);
            Message message = consumer.receive(200000000);
            consumer.close();
            session.close();
            connection.close();
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("Received: " + text);     
            String[] parts=text.split("[/, ]");             
            Registry registry = LocateRegistry.getRegistry(parts[0]);
            Work example = (Work) registry.lookup(parts[1]);
            String answer = (String)example.doWork(tasks.pop());
            System.out.println("Output is:"+answer);
        }
        catch(Exception e){
            e.printStackTrace();
        }       
    }
}

class ExampleWork implements Task{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    static int work=0;
    int timer;
    public ExampleWork(int timer){
        this.timer =timer;
    }
    

    @Override
    
    public Object doWork() {
        work++;
        Object o=null;
        try{
            Thread.sleep(timer*1000);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        o= "work"+work;
        return o;       
    }
    
}
