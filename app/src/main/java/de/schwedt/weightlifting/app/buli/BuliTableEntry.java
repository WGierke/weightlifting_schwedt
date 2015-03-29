package de.schwedt.weightlifting.app.buli;

public class BuliTableEntry {

    private String place;
    private String club;
    private String score;
    private String max_score;
    private String cardinal_points;

    public BuliTableEntry() {

    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getMaxScore() {
        return max_score;
    }

    public void setMaxScore(String max_score) {
        this.max_score = max_score;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCardinalPoints() {
        return cardinal_points;
    }

    public void setCardinalPoints(String cardinal_points) {
        this.cardinal_points = cardinal_points;
    }

}
