package de.schwedt.weightlifting.app.buli;

import android.graphics.drawable.Drawable;

public class BuliTeamMember {

    private String name;
    private String year;
    private String snatching;
    private String jerking;
    private String max_score;
    private String imageURL;
    private Drawable image;

    public BuliTeamMember() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSnatching() {
        return snatching;
    }

    public void setSnatching(String snatching) {
        this.snatching = snatching;
    }

    public String getJerking() {
        return jerking;
    }

    public void setJerking(String jerking) {
        this.jerking = jerking;
    }

    public String getMaxScore() {
        return max_score;
    }

    public void setMaxScore(String max_score) {
        this.max_score = max_score;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
