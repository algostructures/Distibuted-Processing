import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Work extends Remote{
    public Object doWork(Task t) throws RemoteException;    
}
