package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;


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
        /*
         * This constructor is used when we want to create a new vote.
         * 1. Set the username of this vote.
         * 2. Set the vote of this vote.
         * 3. Set the time created of this vote.
         */

        // 1. Set the username of this vote.
        this.username = username;

        // 2. Set the vote of this vote.
        this.vote = vote == VoteType.UPVOTE;

        // 3. Set the time created of this vote.
        this.time_created = new Timestamp(System.currentTimeMillis());
    }
    // Empty constructor
    public Vote() {
    }

    // Getters
    public boolean getVote() {
        return this.vote;
    }

    public String getUsername() {
        return this.username;
    }

    public Timestamp getTime_created() {
        return this.time_created;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public void setTime_created(Timestamp time_created) {
        this.time_created = time_created;
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
