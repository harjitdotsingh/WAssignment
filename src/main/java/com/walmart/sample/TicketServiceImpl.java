package com.walmart.sample;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by harjitsingh on 12/20/15.
 */
public class TicketServiceImpl implements TicketService {


    public static int level1SeatCount = 25 * 50;
    public static int level2SeatCount = 20 * 100;
    public static int level3SeatCount = 15 * 100;
    public static int level4SeatCount = 15 * 100;


    static public TicketServiceImpl create() {

        return new TicketServiceImpl();
    }

    private TicketServiceImpl() {


    }


    Multimap<Integer, Seat> reservedSeats = ArrayListMultimap.create();

    Multimap<Integer, Seat> holdingSeats = ArrayListMultimap.create();


    Multimap<String, SeatHold> currentHolds = HashMultimap.create();

    Multimap<String, SeatHold> currentReservation = HashMultimap.create();


    public Multimap<Integer, Seat> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(Multimap<Integer, Seat> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public Multimap<Integer, Seat> getHoldingSeats() {
        return holdingSeats;
    }

    public void setHoldingSeats(Multimap<Integer, Seat> holdingSeats) {
        this.holdingSeats = holdingSeats;
    }

    public Multimap<String, SeatHold> getCurrentHolds() {
        return currentHolds;
    }

    public void setCurrentHolds(Multimap<String, SeatHold> currentHolds) {
        this.currentHolds = currentHolds;
    }

    public Multimap<String, SeatHold> getCurrentReservation() {
        return currentReservation;
    }

    public void setCurrentReservation(Multimap<String, SeatHold> currentReservation) {
        this.currentReservation = currentReservation;
    }

    @Override
    public int numSeatsAvailable(Optional<Integer> venueLevel) {

        int availableSeats = 0;

        if (venueLevel.isPresent()) {

            availableSeats = getTotalSeatsForLevel(venueLevel) - (Math.abs(reservedSeats.get(venueLevel.get()).size() - holdingSeats.get(venueLevel.get()).size()));
        } else {
            throw new IllegalArgumentException(" VenueLevel Cannot be Null");
        }

        return availableSeats;

    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {


        int mLevel = minLevel.get().intValue();
        int mxLevel = maxLevel.get().intValue();


        if (mLevel > mxLevel) {

            throw new IllegalArgumentException(" Min level is greater than max Level");
        }


        SeatHold seatHold = null;


        if (mLevel == mxLevel) {

            if (numSeats <= this.numSeatsAvailable(Optional.of(mLevel))) {

                Optional<List<Seat>> holdList = this.holdSeatsForARow(numSeats, Optional.of(mLevel));

                if (holdList.isPresent()) {


                    seatHold = generateSeatHold(holdList, Optional.of(mLevel), customerEmail);
                    currentHolds.put(seatHold.getSeatHoldId(), seatHold);


                }

            }
        } else {
            for (int levelCount = mLevel; levelCount <= mxLevel; ++mLevel) {


                if (numSeats <= this.numSeatsAvailable(Optional.of(mLevel))) {

                    Optional<List<Seat>> holdList = this.holdSeatsForARow(numSeats, Optional.of(mLevel));

                    if (holdList.isPresent()) {


                        seatHold = generateSeatHold(holdList, Optional.of(mLevel), customerEmail);
                        currentHolds.put(seatHold.getSeatHoldId(), seatHold);

                        break;


                    }

                } else {
                    continue;
                }
            }


        }

        return seatHold;

    }

    @Override
    public String reserveSeats(String seatHoldId, String customerEmail) {


        String newReservation = UUID.fromString(seatHoldId).toString();


        Collection<SeatHold> holdForCustomer = currentHolds.get(seatHoldId);

        int level;
        for (SeatHold seatHold : holdForCustomer) {
            int llevel = seatHold.getLevel();
            level = llevel;
            seatHold.getSeatLists().forEach(seat -> {

                reservedSeats.put(llevel, seat);


            });

            removeFromHold(level, seatHold.getSeatLists());
            currentReservation.put(newReservation, seatHold);
        }
        return newReservation;

    }


    private void removeFromHold(int level, Collection<Seat> seatCollection) {

        Map map = holdingSeats.asMap();
        map.remove(level, seatCollection);


    }


    public int getTotalSeatsForLevel(Optional<Integer> venueLevel) {
        int totalSeats = 0;
        switch (venueLevel.get()) {

            case 1:
                totalSeats = level1SeatCount;
                break;

            case 2:
                totalSeats = level2SeatCount;
                break;
            case 3:
                totalSeats = level3SeatCount;
                break;
            case 4:
                totalSeats = level4SeatCount;

                break;

        }


        return totalSeats;
    }


    @VisibleForTesting
    SeatHold generateSeatHold(Optional<List<Seat>> holdList, Optional<Integer> level, String customerEmail) {

        SeatHold seatHold = SeatHold.create();

        seatHold.setCustomerEmail(customerEmail);
        seatHold.setSeatLists(holdList.get());
        seatHold.setLevel(level.get());
        holdList.get().forEach(seat -> holdingSeats.put(level.get(), seat));
        return seatHold;

    }

    @VisibleForTesting
    Optional<List<Seat>> holdSeatsForARow(int numberOfSeats, Optional<Integer> level) {


        Collection<Seat> seats = reservedSeats.get(level.get());

        Optional<List<Seat>> holdList = Optional.empty();
        for (int row = 0; row < getRowsForLevel(level); ++row) {
            int currentRow = row;
            Collection<Seat> seatsForRow = seats.stream().filter(s -> s.rowid == currentRow).collect(Collectors.toList());

            if (seatsForRow.size() >= numberOfSeats) {


                // We have got a match....

                holdList = holdSeatsForALevel(level, (currentRow + 1), numberOfSeats, seatsForRow.size());

                break;

            } else if (seats.size() == 0) {

                holdList = holdSeatsForALevel(level, (currentRow + 1), numberOfSeats, 1);
                break;

            }


        }


        return holdList;

    }


    @VisibleForTesting
    private Optional<List<Seat>> holdSeatsForALevel(Optional<Integer> level, int currentRow, int numberOfSeats, int startingSeatNumber) {

        List<Seat> holdSeatList = Lists.newArrayList();
        for (int currentSeatCount = 0; currentSeatCount < numberOfSeats; ++currentSeatCount) {

            Seat seat = Seat.create();

            seat.setRowid(currentRow);
            seat.setSeatNumber(currentSeatCount + startingSeatNumber);
            holdSeatList.add(seat);


        }
        return Optional.of(holdSeatList);

    }


    @VisibleForTesting
    int getRowsForLevel(Optional<Integer> level) {

        int totalSeats = 0;

        switch (level.get()) {

            case 1:
                totalSeats = level1SeatCount / 50;
                break;

            case 2:
                totalSeats = level2SeatCount / 100;
                break;
            case 3:
                totalSeats = level3SeatCount / 100;
                break;
            case 4:
                totalSeats = level4SeatCount / 100;

                break;

        }
        return totalSeats;
    }

}
