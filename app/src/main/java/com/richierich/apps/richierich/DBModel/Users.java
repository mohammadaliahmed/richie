package com.richierich.apps.richierich.DBModel;

/**
 * Created by M Ali Ahmed on 9/21/2017.
 */

public class Users {
    String username, email, password, phone,time;



    public Users() {
    }

    public Users(String username, String email, String password, String phone,String time) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.time=time;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
