package com.example.azzem.chatty.Model;

public class Example_item
{
    private int mImageRessource;
    private String mText1, mText2;

    public Example_item(int mImageRessource, String mText1, String mText2)
    {
        this.mImageRessource = mImageRessource;
        this.mText1 = mText1;
        this.mText2 = mText2;
    }

    public int getmImageRessource()
    {
        return mImageRessource;
    }

    public String getmText1()
    {
        return mText1;
    }

    public String getmText2()
    {
        return mText2;
    }
}
