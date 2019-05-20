package com.example.azzem.chatty.Adapter;

import android.content.Context;

import com.example.azzem.chatty.Model.User;

import java.util.List;

public class participantAdapter
{
    private List<User> mPartcipants;
    private Context mContext;

    public participantAdapter(List<User> mPartcipants, Context mContext) {
        this.mPartcipants = mPartcipants;
        this.mContext = mContext;
    }
}
