package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

public class Comment extends User_interaction {
    // Member variables
    private String comment;

    // Constructor
    public Comment(String username, String comment) {
        /*
        * This constructor is used when we want to create a new comment.
        *
        * 1. Set the username of this comment.
        * 2. Set the comment of this comment.
        * 3. Set the time created of this comment.
        *
        */

        // 1. Set the username of this comment.
        this.comment = comment;

        // 2. Set the comment of this comment.
        this.username = username;

        // 3. Set the time created of this comment.
        this.time_created = new Timestamp(System.currentTimeMillis());
    }

    // Getters
    public String getComment() {
        return this.comment;
    }

    public String getUsername() {
        return this.username;
    }

    public Timestamp getTime_created() {
        return this.time_created;
    }

    // Setters
    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTime_created(Timestamp time_created) {
        this.time_created = time_created;
    }

    // Methods
    @Override
    public void JSON_write(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), this);
    }

    public static Comment JSON_read(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Comment.class);
    }
}
