package com.glovoapp.backender;

/**
 * To be used for exposing order information through the API
 */
public class OrderVM {
    // <TODO> remove field
    private double pickupDistance;
    private int slot;
    private boolean vip;
    private boolean food;
    //-------
    private String id;
    private String description;

    public OrderVM(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    //<TODO> remove this methods
    public OrderVM(String id, String description, int slot, double pickupDistance, boolean vip, boolean food) {
        this.id = id;
        this.description = description;
        this.slot = slot;
        this.pickupDistance = pickupDistance;
        this.vip = vip;
        this.food = food;
    }

    public double getPickupDistance() {
        return pickupDistance;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isVip() {
        return vip;
    }

    public boolean isFood() {
        return food;
    }
}
