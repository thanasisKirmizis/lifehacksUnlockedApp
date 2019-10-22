package com.kirman.lifehacksunlocked;

public class CardEntity {

    private int id;
    private String tip;
    private String category;
    private boolean bookmarked;
    private boolean newTip;

    public CardEntity() {
        //Empty constructor
    }

    public CardEntity(String tip, String category, int id) {
        this.tip = tip;
        this.category = category;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public boolean isNewTip() {
        return newTip;
    }

    public void setNewTip(boolean newTip) {
        this.newTip = newTip;
    }
}
