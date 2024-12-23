package models;

import java.io.Serializable;
import java.util.Date;

public class Bid implements Serializable {
    private int bidId;
    private int itemId;
    private String bidder;
    private double amount;
    private Date bidTime;

    public Bid(int bidId, int itemId, String bidder, double amount) {
        this.bidId = bidId;
        this.itemId = itemId;
        this.bidder = bidder;
        this.amount = amount;
        this.bidTime = new Date();
    }

    // Getters
    public int getBidId() {
        return bidId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getBidder() {
        return bidder;
    }

    public double getAmount() {
        return amount;
    }

    public Date getBidTime() {
        return bidTime;
    }
}
