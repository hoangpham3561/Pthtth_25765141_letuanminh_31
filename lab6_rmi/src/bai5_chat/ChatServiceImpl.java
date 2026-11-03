package bai5_chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {
    private String lastReply;

    public ChatServiceImpl() throws RemoteException {
        super();
        this.lastReply = "Chua co phan hoi tu server.";
    }

    @Override
    public synchronized void sendMessage(String message) throws RemoteException {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[ChatServer " + time + "] Nhan tin nhan: " + message);
        this.lastReply = "Server da nhan thanh cong luc " + time + ": \"" + message + "\"";
    }

    @Override
    public synchronized String getLastReply() throws RemoteException {
        return this.lastReply;
    }
}
