package com.richierich.apps.richierich.DBModel;

/**
 * Created by maliahmed on 10/26/2017.
 */

public class CompletedHundered
{
    String completed;
    String info;

    public CompletedHundered() {
    }

    public CompletedHundered(String completed, String info) {
        this.completed = completed;
        this.info = info;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
