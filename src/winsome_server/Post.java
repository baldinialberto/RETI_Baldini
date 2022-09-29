package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Comment_simple;
import winsome_comunication.Post_detailed;
import winsome_comunication.Post_simple;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Post implements JSON_Serializable, Comparable<Post> {

    // Member variables
    private String id;
    private String author;

    private String title;
    private String text;
    private ArrayList<Comment> comments;
    private ArrayList<Vote> votes;

    private Timestamp time_created;

    // Constructors
    // Default Constructor
    public Post(String id, String author, String title, String text) {
        /*
         * This constructor is used when we want to create a new post.
         *
         * 1. Create a new post id for this post.
         * 2. Set the author of this post.
         * 3. Set the title of this post.
         * 4. Set the text of this post.
         * 5. Create a new list of comments for this post.
         * 6. Create a new list of votes for this post.
         * 7. Set the time_created Timestamp
         */

        // 1. Create a new post id for this post.
        this.id = id;

        // 2. Set the author of this post.
        this.author = author;

        // 3. Set the title of this post.
        this.title = title;

        // 4. Set the text of this post.
        this.text = text;

        // 5. Create a new list of comments for this post.
        this.comments = new ArrayList<>();

        // 6. Create a new list of votes for this post.
        this.votes = new ArrayList<>();

        // 7. Set the time_created Timestamp
        this.time_created = new Timestamp(System.currentTimeMillis());
    }

    // Jackson Constructor
    public Post()
    {

    }

    // Methods
    public void addComment(Comment comment) {
        /*
         * This method is used to add a comment to the list of comments of this post.
         *
         * 1. Add the comment to the list of comments of this post.
         */

        // 1. Add the comment to the list of comments of this post.
        this.comments.add(comment);
    }

    public void addVote(Vote vote) {
        /*
         * This method is used to add a vote to the list of votes of this post.
         *
         * 1. Add the vote to the list of votes of this post.
         */

        // 1. Add the vote to the list of votes of this post.
        this.votes.add(vote);
    }

    public Post_simple to_post_simple() {
        /*
         * This method is used to get a Post_simple object from this Post object.
         *
         * 1. Return the Post_simple object.
         */

        // 1. Return the Post_simple object.
        return new Post_simple(this.title, this.text, this.author, this.id);
    }
    public Post_detailed to_post_detailed() {
        /*
        * This method is used to get a Post_detailed object from this Post object.
        *
        * 1. Return the Post_detailed object.
        */

        // 1. Return the Post_detailed object.
        int upvotes = 0;
        int downvotes = 0;
        for (Vote vote : this.votes) {
            if (vote.getVote()) {
                upvotes++;
            } else {
                downvotes++;
            }
        }
        ArrayList<Comment_simple> comments = new ArrayList<>();
        for (Comment comment : this.comments) {
            comments.add(comment.to_comment_simple());
        }
        return new Post_detailed(this.title, this.text, this.author, this.id, upvotes, downvotes, comments);
    }
    public boolean has_vote_from(String user) {
        /*
         * This method is used to check if a user has voted on this post.
         *
         * 1. Loop through the list of votes.
         * 2. If the user has voted on this post, return true.
         */

        // 1. Loop through the list of votes.
        for (Vote vote : this.votes) {
            // 2. If the user has voted on this post, return true.
            if (vote.getUsername().equals(user)) {
                return true;
            }
        }
        return false;
    }
    // Getters

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public Timestamp getTime_created()
    {
        return time_created;
    }

    // Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setVotes(ArrayList<Vote> votes) {
        this.votes = votes;
    }

    public void setTime_created(Timestamp time_created)
    {
        this.time_created = time_created;
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


    @Override
    public int compareTo(Post o) {
        /*
         * This method is used to compare this Post object with another Post object.
         * the comparison is based on the time_created field, the most recent post is the smallest.
         */

        return this.time_created.compareTo(o.time_created);
    }


}
