package com.example.azzem.chatty.Model;


import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageG
{
    public String msg;
    public String from;
    public String type;

    public MessageG(String msg, String from, String type) {
        this.msg = msg;
        this.from = from;
        this.type = type;
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
}
