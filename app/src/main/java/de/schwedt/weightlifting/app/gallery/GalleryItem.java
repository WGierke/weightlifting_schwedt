package de.schwedt.weightlifting.app.gallery;


public class GalleryItem {

    private String title;
    private String[] imageUrls;
    private String url;

    public GalleryItem() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getImageUrls() {
        return this.imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getImageUrl(int index) {
        return ((index < imageUrls.length) ? imageUrls[index] : null);
    }
}