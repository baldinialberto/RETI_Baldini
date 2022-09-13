package winsome;

import java.util.Collection;

public class Vote {
    private final VoteEnum vote;
    private final User owner;

    public Vote(VoteEnum vote, User owner) {
        this.vote = vote;
        this.owner = owner;
    }

    public int get_value() {
        return vote.equals(VoteEnum.upVote) ? 1 : -1;
    }

    public static Pair<Integer, Integer> get_votes_count(Collection<Vote> votes) {
        int total = 0;
        int size = votes.size();
        for (Vote v : votes) {
            total += v.get_value();
        }
        return new Pair<Integer, Integer>((size + total) / 2, size - ((size + total) / 2));
    }
}
