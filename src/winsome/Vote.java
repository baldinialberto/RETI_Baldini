package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;


public class Vote extends User_interaction {
    // Member variables

    // vote == true means upvote, vote == false means downvote
    private boolean vote;
    public enum VoteType {
        UPVOTE,
        DOWNVOTE
    }

    // Constructor
    public Vote(String username, VoteType vote)
    {
        this.vote = vote == VoteType.UPVOTE;
        this.username = username;
    }

    // Getters
    public boolean getVote() {
        return this.vote;
    }

    public String getUsername() {
        return this.username;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }


    // Other methods
    @Override
    public void JSON_write(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), this);
    }

    public static Vote JSON_read(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Vote.class);
    }
}
