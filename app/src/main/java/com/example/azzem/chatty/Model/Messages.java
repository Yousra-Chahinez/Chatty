package com.example.azzem.chatty.Model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Messages
{
    private String documentId, message, type, from;
    private String time;
    private boolean seen;
    @ServerTimestamp
    private Date date;

    //Help to get the messages
    public Messages(String message, boolean seen, String time, String type, String from)
    {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;
    }
    public Messages()
    {
    }
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean getSeen()
    {
        return seen;
    }

    public void setSeen(boolean seen)
    {
        this.seen = seen;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

/*
* package com.example.azzem.chatty.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Messages
{
    private String message, type, from;
    private @ServerTimestamp Date timestamp;
    private boolean seen;
    //Help to get the messages
    public Messages(String message, boolean seen, Date timestamp, String type, String from)
    {
        this.message = message;
        this.seen = seen;
        this.timestamp = timestamp;
        this.type = type;
        this.from = from;
    }
    public Messages()
    {
    }
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean getSeen()
    {
        return seen;
    }

    public void setSeen(boolean seen)
    {
        this.seen = seen;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

}
*/