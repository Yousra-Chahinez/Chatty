package com.example.azzem.chatty.Model;

import java.util.ArrayList;
import java.util.List;

public class Groups {
    private String groupName;
    private String Admin;
    private String push_id;
    public MessageG messageG;
    private String members;

    public Groups() {
    }

    public Groups(String groupName, String Admin, String push_id, MessageG messageG, String members) {
        this.groupName = groupName;
        this.Admin = Admin;
        this.push_id = push_id;
        this.messageG = messageG;
        this.members = members;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public MessageG getMessageG() {
        return messageG;
    }
    public void setMessageG(MessageG messageG) {
        this.messageG = messageG;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }
}

