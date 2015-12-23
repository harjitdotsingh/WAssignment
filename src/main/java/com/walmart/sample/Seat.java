package com.walmart.sample;

/**
 * Created by harjitsingh on 12/20/15.
 */
public class Seat implements Comparable<Seat> {


    int rowid;
    int seatNumber;


    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }


    static public Seat create() {


        return new Seat();
    }

    private Seat() {


    }

    @Override
    public String toString() {
        return "Seat{" +
                "rowid=" + rowid +
                ", seatNumber=" + seatNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return com.google.common.base.Objects.equal(rowid, seat.rowid) &&
                com.google.common.base.Objects.equal(seatNumber, seat.seatNumber);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(rowid, seatNumber);
    }

    public int getSeatNumber() {

        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public int compareTo(Seat o) {

        int sortIndex = 0;
        if (rowid < o.rowid && seatNumber < o.seatNumber) {

            sortIndex = -1;
        }
        if (rowid == o.rowid && seatNumber == o.seatNumber) {
            sortIndex = 0;
        } else if (rowid > o.rowid && seatNumber > o.seatNumber) {
            {

                sortIndex = 1;
            }
        }
        return sortIndex;
    }

}
