package com.example.android_assigment;

public class Messege {

    private String time;
    private String date;
    private String userName;
    private String message;

    public Messege() {}

    public Messege(String time, String date, String userName, String message) {
        this.time = time;
        this.date = date;
        this.userName = userName;
        this.message = message;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
