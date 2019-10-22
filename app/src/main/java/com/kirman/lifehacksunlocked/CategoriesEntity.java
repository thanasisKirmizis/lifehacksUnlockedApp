package com.kirman.lifehacksunlocked;

public class CategoriesEntity {

    private String mTitle;
    private int mIconID;

    public CategoriesEntity(){
        //Empty Constructor
    }

    public CategoriesEntity(String title, int iconID) {
        mTitle = title;
        mIconID = iconID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getIconID() {
        return mIconID;
    }

    public void setIconID(int iconID) {
        mIconID = iconID;
    }
}
