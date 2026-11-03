package bai5_chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {
    void sendMessage(String message) throws RemoteException;
    String getLastReply() throws RemoteException;
}
