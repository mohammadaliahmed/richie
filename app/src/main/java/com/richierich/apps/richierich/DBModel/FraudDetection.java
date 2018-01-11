package com.richierich.apps.richierich.DBModel;

/**
 * Created by M Ali Ahmed on 9/21/2017.
 */

public class FraudDetection {
        String date;
        int remaining;

    public FraudDetection() {
    }

    public FraudDetection(String date, int remaining) {
        this.date = date;
        this.remaining = remaining;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
