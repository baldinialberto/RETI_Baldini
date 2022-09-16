package winsome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;


public class Vote extends User_interaction {

    // vote == true means upvote, vote == false means downvote
    private final boolean vote;
    public enum VoteType {
        UPVOTE,
        DOWNVOTE
    }
    public Vote(String username, VoteType vote)
    {
        super();
        this.vote = vote == VoteType.UPVOTE;
    }

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
