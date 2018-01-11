package com.richierich.apps.richierich.DBModel;

/**
 * Created by M Ali Ahmed on 9/21/2017.
 */

public class AccountDetails {

    int clicksDone;
    double amountEarned;
    int level;

    public AccountDetails() {
    }

    public AccountDetails(int clicksDone, double amountEarned, int level) {
        this.clicksDone = clicksDone;
        this.amountEarned = amountEarned;
        this.level=level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getClicksDone() {
        return clicksDone;
    }

    public void setClicksDone(int clicksDone) {
        this.clicksDone = clicksDone;
    }

    public double getAmountEarned() {
        return amountEarned;
    }

    public void setAmountEarned(double amountEarned) {
        this.amountEarned = amountEarned;
    }
}
