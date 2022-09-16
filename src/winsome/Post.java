package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Post implements JSON_Serializable {


    // Member variables
    private Post_ID id;
    private String author;
    private String text;
    private List<Comment> comments;
    private List<Vote> votes;

    // Constructor
    public Post(String author, String text) {
        /*
         * This constructor is used when we want to create a new post.
         *
         * 1. Create a new post id for this post.
         * 2. Set the author of this post.
         * 3. Set the text of this post.
         * 4. Create a new list of comments for this post.
         * 5. Create a new list of votes for this post.
         */

        // 1. Create a new post id for this post.
        this.id = new Post_ID();

        // 2. Set the author of this post.
        this.author = author;

        // 3. Set the text of this post.
        this.text = text;

        // 4. Create a new list of comments for this post.
        this.comments = new ArrayList<>();

        // 5. Create a new list of votes for this post.
        this.votes = new ArrayList<>();
    }

    // Methods

    // Getters

    public Post_ID getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    // Setters

    public void setId(Post_ID id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    // Interface methods
    @Override
    public void JSON_write(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), this);
    }

    public static Post JSON_read(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Post.class);
    }
}
