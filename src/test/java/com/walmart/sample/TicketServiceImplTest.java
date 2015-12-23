package com.walmart.sample;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by harjitsingh on 12/20/15.
 */
public class TicketServiceImplTest {


    TicketServiceImpl tickethandler;

    @Before
    public void setUp() throws Exception {
        tickethandler = TicketServiceImpl.create();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNumSeatsAvailable() throws Exception {


        int levelCount = tickethandler.numSeatsAvailable(Optional.of(1));


        org.junit.Assert.assertEquals(TicketServiceImpl.level1SeatCount, levelCount);

    }

    @Test
    public void testFindAndHoldSeats() throws Exception {


        SeatHold seatHold = tickethandler.findAndHoldSeats(2, Optional.of(1), Optional.of(2), "test@test.com");

        Assert.assertNotNull(seatHold);
        Assert.assertEquals(2, seatHold.getSeatLists().size());
        Assert.assertNotNull(seatHold.getSeatHoldId());
        Assert.assertEquals(seatHold.getCustomerEmail(), "test@test.com");
        Assert.assertEquals(2, tickethandler.getHoldingSeats().size());
        int remainingSeats = tickethandler.numSeatsAvailable(Optional.of(1));

        org.junit.Assert.assertEquals((TicketServiceImpl.level1SeatCount) - 2, remainingSeats);


        // this will return a nul because we have already booked 2 seats and hence it will not generate a hold
        seatHold = tickethandler.findAndHoldSeats(1600, Optional.of(1), Optional.of(1), "test@test.com");

        Assert.assertNull(seatHold);


    }

    @Test
    public void testReserveSeats() throws Exception {


        SeatHold seatHold = tickethandler.findAndHoldSeats(2, Optional.of(2), Optional.of(2), "test@test.com");

        Assert.assertNotNull(seatHold);
        Assert.assertEquals(2, seatHold.getSeatLists().size());
        Assert.assertNotNull(seatHold.getSeatHoldId());
        Assert.assertEquals(seatHold.getCustomerEmail(), "test@test.com");
        Assert.assertEquals(2, tickethandler.getHoldingSeats().size());

        String newReservation = tickethandler.reserveSeats(seatHold.getSeatHoldId(), "test@test.com");

        Assert.assertNotNull(newReservation);
        Assert.assertEquals(2, tickethandler.getReservedSeats().size());
        Assert.assertEquals(0, tickethandler.getHoldingSeats().size());

        int remainingSeats = tickethandler.numSeatsAvailable(Optional.of(2));

        org.junit.Assert.assertEquals((TicketServiceImpl.level2SeatCount) - 2, remainingSeats);


    }

    @Test
    public void testCreate() throws Exception {


        TicketService ticketService = TicketServiceImpl.create();

        Assert.assertNotNull(ticketService);

    }


    @Test
    public void testGetTotalSeatsForLevel() throws Exception {
        int totalSeatsForLevel = tickethandler.getTotalSeatsForLevel(Optional.of(1));
        Assert.assertEquals(totalSeatsForLevel, TicketServiceImpl.level1SeatCount);

    }


}