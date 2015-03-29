package de.schwedt.weightlifting.app.news;

import de.schwedt.weightlifting.app.helper.DataHelper;

public class EventItem {

    private String title;
    private String date;
    private String location;
    private String preview;

    public EventItem() {

    }

    public EventItem(String heading, String content, String date, String location) {
        this.title = heading;
        this.date = date;
        this.location = location;
        setPreview(DataHelper.trimString(content, 150));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setPreview(DataHelper.trimString(title, 50));
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
