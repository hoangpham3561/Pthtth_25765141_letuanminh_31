package bai9_daugia;

import java.io.Serializable;

public class AuctionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private double currentPrice;
    private String highestBidder;

    public AuctionItem(String id, String name, double startPrice) {
        this.id = id;
        this.name = name;
        this.currentPrice = startPrice;
        this.highestBidder = "Chưa có";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized double getCurrentPrice() {
        return currentPrice;
    }

    public synchronized String getHighestBidder() {
        return highestBidder;
    }

    public synchronized boolean bid(String bidder, double amount) {
        if (amount > this.currentPrice) {
            this.currentPrice = amount;
            this.highestBidder = bidder;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Giá hiện tại: %.2f | Người đặt giá cao nhất: %s",
                id, name, currentPrice, highestBidder);
    }
}
