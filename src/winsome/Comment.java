package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class Comment extends User_interaction {
    // Member variables
    private String comment;

    // Constructor
    public Comment(String username, String comment) {
        this.comment = comment;
        this.username = username;
    }

    // Getters
    public String getComment() {
        return this.comment;
    }

    public String getUsername() {
        return this.username;
    }

    // Setters
    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUsername(String username) {
        this.username = username;
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
