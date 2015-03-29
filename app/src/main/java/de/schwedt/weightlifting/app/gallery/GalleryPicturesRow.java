package de.schwedt.weightlifting.app.gallery;


import java.util.ArrayList;

public class GalleryPicturesRow {


    private ArrayList<String> items;

    public GalleryPicturesRow() {
        items = new ArrayList<String>();
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public String getItem(int index) {
        return ((index < items.size()) ? items.get(index) : null);
    }

    public void addGalleryPicture(String imageUrl) {
        items.add(imageUrl);
    }
}