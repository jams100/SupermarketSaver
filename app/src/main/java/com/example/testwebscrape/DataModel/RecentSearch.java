package com.example.testwebscrape.DataModel;

public class RecentSearch {

    private String name;
    private int recentImage;
    private int leftImage;

    public RecentSearch() {
    }

    public RecentSearch(String name, int recentImage, int leftImage) {
        this.name = name;
        this.recentImage = recentImage;
        this.leftImage = leftImage;
    }

    public String getName() {
        return name;
    }

    public int getRecentImage() {
        return recentImage;
    }

    public int getLeftImage() {
        return leftImage;
    }
}
