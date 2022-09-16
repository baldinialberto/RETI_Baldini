package winsome;

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

    }

    @Override
    public void JSON_read(String filePath) throws IOException {

    }
}
