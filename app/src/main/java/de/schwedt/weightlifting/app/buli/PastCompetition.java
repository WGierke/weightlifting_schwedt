package de.schwedt.weightlifting.app.buli;

import de.schwedt.weightlifting.app.UpdateableItem;

public class PastCompetition extends UpdateableItem {

    private String location;
    private String date;
    private String home;
    private String guest;
    private String score;
    private String url;

    public PastCompetition() {

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getProtocolUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
