package com.example.azzem.chatty.Model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Chat{
    private String documentid;
    private List<String> participants;
    private String receiverId;
    private String receiverName;
    private String receiverImage;

    public Chat(List<String> participants, String receiverName, String receiverImage, String receiverId) {
        this.participants = participants;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.receiverId = receiverId;
    }

    public Chat() {
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    @Exclude
    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }
}