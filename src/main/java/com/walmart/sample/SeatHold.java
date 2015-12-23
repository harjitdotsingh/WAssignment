package com.walmart.sample;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by harjitsingh on 12/20/15.
 */
public class SeatHold {

    // Id for the Hold
    String seatHoldId;
    // Customer Requesting it
    String customerEmail;

    int level;


    // Time the hold was requested. This was be later used to Expire it
    long timeReserved;

    // list of Seats allocated to be held  by a customer.
    Collection<Seat> seatLists = Lists.newArrayList();


    static public SeatHold create() {

        return new SeatHold();
    }

    private SeatHold() {


        this.seatHoldId = UUID.randomUUID().toString();

        timeReserved = System.currentTimeMillis();
    }


    public String getSeatHoldId() {
        return seatHoldId;
    }

    public void setSeatHoldId(String seatHoldId) {
        this.seatHoldId = seatHoldId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }


    public Collection<Seat> getSeatLists() {
        return seatLists;
    }

    public void setSeatLists(List<Seat> seatLists) {
        this.seatLists = seatLists;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
