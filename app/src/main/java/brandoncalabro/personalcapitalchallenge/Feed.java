package brandoncalabro.personalcapitalchallenge;

@SuppressWarnings("WeakerAccess")
public class Feed {
    private String title;
    private String image_url;
    private String description;
    private String pub_date;
    private String article_url;

    /**
     * a simple helper class to store the feed item data
     *
     * @param title       each article has an html encoded title represented in the title node
     * @param image_url   an image is represented in the media:content node within the url attribute
     * @param description a quick html encoded summary of the article is represented in the description node
     * @param pub_date    a published date is represented in the pubDate node
     * @param article_url a link to the actual article is represented in the link node
     */
    public Feed(String title, String image_url, String description, String pub_date, String article_url) {
        this.title = title;
        this.image_url = image_url;
        this.description = description;
        this.pub_date = pub_date;
        this.article_url = article_url;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDescription() {
        return description;
    }

    public String getPub_date() {
        return pub_date;
    }

    public String getArticle_url() {
        return article_url;
    }
}
