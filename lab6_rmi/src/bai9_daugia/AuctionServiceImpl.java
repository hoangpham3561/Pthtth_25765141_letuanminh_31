package bai9_daugia;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionServiceImpl extends UnicastRemoteObject implements AuctionService {
    private final Map<String, AuctionItem> itemMap;

    public AuctionServiceImpl() throws RemoteException {
        super();
        this.itemMap = new ConcurrentHashMap<>();
        itemMap.put("SP01", new AuctionItem("SP01", "Tranh phong cảnh cổ điển", 500.0));
        itemMap.put("SP02", new AuctionItem("SP02", "Đồng hồ Thụy Sỹ Vintage", 1200.0));
        itemMap.put("SP03", new AuctionItem("SP03", "Bình gốm thời Minh", 3000.0));
    }

    @Override
    public synchronized List<AuctionItem> getItems() throws RemoteException {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public synchronized boolean placeBid(String itemId, String bidder, double amount) throws RemoteException {
        if (itemId == null || bidder == null) return false;
        AuctionItem item = itemMap.get(itemId.toUpperCase());
        if (item == null) return false;

        boolean success = item.bid(bidder, amount);
        if (success) {
            System.out.printf("[AuctionServer] THÔNG BÁO: %s da dat gia moi cho [%s - %s]: %.2f (Nguoi dan dau: %s)\n",
                    bidder, item.getId(), item.getName(), amount, bidder);
        } else {
            System.out.printf("[AuctionServer] TU CHOI: %s tra gia %.2f cho [%s], thap hon hoac bang gia hien tai (%.2f)\n",
                    bidder, amount, item.getId(), item.getCurrentPrice());
        }
        return success;
    }

    @Override
    public synchronized AuctionItem getItem(String itemId) throws RemoteException {
        if (itemId == null) return null;
        return itemMap.get(itemId.toUpperCase());
    }
}
