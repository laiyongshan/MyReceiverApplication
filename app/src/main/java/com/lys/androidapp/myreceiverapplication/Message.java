package com.lys.androidapp.myreceiverapplication;

/**
 * @author lys
 * @time 2018/7/19 14:41
 * @desc:
 */

public class Message {

    private String message;

    private String from;

    public Message(String from, String message){
        this.message=message;
        this.from=from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}
