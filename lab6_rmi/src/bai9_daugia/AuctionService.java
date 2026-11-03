package bai9_daugia;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AuctionService extends Remote {
    List<AuctionItem> getItems() throws RemoteException;
    boolean placeBid(String itemId, String bidder, double amount) throws RemoteException;
    AuctionItem getItem(String itemId) throws RemoteException;
}
