package models;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {
    private int itemId;
    private String name;
    private String description;
    private double startingPrice;
    private double currentPrice;
    private Date endTime;
    private String seller;
    private boolean isActive;
    private boolean isPaid;
    private String winningBidder;

    public Item(int itemId, String name, String description, double startingPrice, Date endTime, String seller) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.endTime = endTime;
        this.seller = seller;
        this.isActive = true;
        this.isPaid = false;
        this.winningBidder = null;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSeller() {
        return seller;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    public String getWinningBidder() {
        return winningBidder;
    }

    public void setWinningBidder(String winningBidder) {
        this.winningBidder = winningBidder;
    }
}
