package com.example.udacity.showme;


public class Movie {
    private String Poster , Id , Overview , Vote_average , Original_title , Release_date ;
    private int Favorite = 0;

    public Movie(){
    }

    public Movie(String poster, String id, String overview, String vote_average, String original_title, String release_date ,int i){
        this.Poster = poster;
        this.Id = id;
        this.Overview = overview;
        this.Vote_average = vote_average;
        this.Original_title = original_title;
        this.Release_date = release_date;
        this.Favorite = i;
    }

    public String getPoster() {
        return Poster;
    }
    public String getId() {
        return Id;
    }
    public String getOverview() {
        return Overview;
    }
    public String getVote_average() {
        return Vote_average;
    }
    public String getOriginal_title() {
        return Original_title;
    }
    public String getRelease_date() {
        return Release_date;
    }
    public int getFavorite() {
        return Favorite;
    }
}
