package com.example.azzem.chatty.Model;


import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class MessageG
{
    private String msg;
    private String from;
    private String type;
    @ServerTimestamp
    private Date date;

    public MessageG(String msg, String from, String type) {
        this.msg = msg;
        this.from = from;
        this.type = type;
        //this.date = date;
    }

    public MessageG() {}


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
