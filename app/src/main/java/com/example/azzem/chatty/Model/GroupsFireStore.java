package com.example.azzem.chatty.Model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class GroupsFireStore
{
    //@ServerTimestamp Date timeStamp;
    private String documentId;
    private List<String> participants;
    private List<String> participants_names;
    private String GroupName;
    private String Admin;
    private String imageUrl;

    public GroupsFireStore(List<String> participants, String groupName, String admin, String imageUrl)
    {
        this.participants = participants;
        GroupName = groupName;
        Admin = admin;
        this.imageUrl = imageUrl;
    }

    public GroupsFireStore() {
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    //If i don't want this document appear in firestore database --> redundant data !
    //--> I will exclude this @Exclude.
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public List<String> getParticipants_names() {
        return participants_names;
    }

    public void setParticipants_names(List<String> participants_names) {
        this.participants_names = participants_names;
    }
}
