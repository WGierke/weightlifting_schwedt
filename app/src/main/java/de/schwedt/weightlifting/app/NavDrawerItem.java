package de.schwedt.weightlifting.app;

public class NavDrawerItem {

    private String title;
    private int icon;
    private int count = 0;

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, int icon, int count) {
        this.title = title;
        this.icon = icon;
        this.count = count;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount(int size) {
        count += size;
    }

    public void decreaseCount(int size) {
        count -= size;
    }
}
