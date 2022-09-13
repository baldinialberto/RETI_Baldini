package winsome;

public class Comment {
    private String text;
    private final User owner;

    public Comment(User owner, String text) {
        this.owner = owner;
        this.text = text;
    }
}
