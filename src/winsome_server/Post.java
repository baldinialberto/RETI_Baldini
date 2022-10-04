package winsome_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Comment_simple;
import winsome_comunication.Post_detailed;
import winsome_comunication.Post_simple;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class Post implements JSON_Serializable, Comparable<Post> {

    // Member variables
    private String id;
    private String author;
    private String title;
    private String text;
    private ArrayList<Comment> comments;
    private ArrayList<Vote> votes;
    private Timestamp time_created;

    private int n_rewards;
    private Timestamp time_last_reward;

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
         * 8. Set the number of rewards to 0.
         * 9. Set the time_last_reward Timestamp.
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

        // 8. Set the number of rewards to 0.
        this.n_rewards = 0;

        // 9. Set the time_last_reward Timestamp.
        this.time_last_reward = new Timestamp(this.time_created.getTime());
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

    public List<Winsome_Reward> calculate_rewards() {
        /*
         * This method is used to calculate the reward of this post.
         *
         * 1. Get the list of votes and comments newer than the time_last_reward.
         * 2. Calculate the reward of this post.
         * 3. Return the reward of this post.
         */

        class Pair<A, B>{
            public A first;
            public B second;
            public Pair(A first, B second) {
                this.first = first;
                this.second = second;
            }
        }

        double res = .0;

        double author_share = .7;
        double curator_share = .3;

        // 1. Get the list of votes and comments newer than the time_last_reward.
        ArrayList<Vote> votes_to_elaborate = new ArrayList<>();
        ArrayList<Vote> votes_known = new ArrayList<>(votes);
        ArrayList<Comment> comments_to_elaborate = new ArrayList<>();
        ArrayList<Comment> comments_known = new ArrayList<>(comments);



        ArrayList<Pair<String, Integer>> users_commented = new ArrayList<>();
        ArrayList<Pair<String, Integer>> users_voted = new ArrayList<>();



        for (Vote vote : this.votes) {
            if (vote.getTime_created().after(this.time_last_reward)) {
                votes_to_elaborate.add(vote);
            }
        }
        for (Comment comment : this.comments) {
            if (comment.getTime_created().after(this.time_last_reward)) {
                comments_to_elaborate.add(comment);
            }
        }

        if (comments_to_elaborate.size() == 0 && votes_to_elaborate.size() == 0) {
            return new ArrayList<>();
        }
        n_rewards++;
        // 2. Calculate the reward of this post.
        // votes
        // logn(max(Sum(p = 0 to new_votes) vote[+1 for upvote, -1 for downvote], 0) +1)
        double sum = 0;
        for (Vote vote : votes_to_elaborate) {
            if (vote.getVote()) {
                sum += 1;
                users_voted.add(new Pair<>(vote.getUsername(), 1));
            } else {
                sum -= 1;
            }
        }
        res += Math.log(Math.max(sum, 0) + 1);

        // comments
        // logn(Sum(p = 0 to new_comments) (2/(1 + e^-(comments_made_by_user -1)) + 1
        sum = 0;
        for (Comment comment : comments_to_elaborate) {
            users_commented.add(new Pair<>(comment.getUsername(), 1));
            int comments_made_by_user = 0;
            for (Comment comment2 : comments_known) {
                if (comment2.getUsername().equals(comment.getUsername())) {
                    comments_made_by_user++;
                    users_commented.get(users_commented.size() - 1).second++;
                }
            }
            sum += 2 / (1 + Math.exp(-(comments_made_by_user-1))) + 1;
        }
        res += Math.log(sum);

        // res /= n_rewards;
        res /= Math.max(n_rewards, 1);

        // 3. Return the reward of this post.
        double author_reward = res * author_share;
        double curator_reward = res * curator_share;
        ArrayList<Winsome_Reward> rewards = new ArrayList<>();
        rewards.add(new Winsome_Reward(author_reward, this.author));

        return rewards;

//        Integer max_comments = 0;
//        for (Pair<String, Integer> pair : users_commented) {
//            if (pair.second > max_comments) {
//                max_comments = pair.second;
//            }
//        }
//
//        ArrayList<Pair<String, Double>> curator_rewards = new ArrayList<>();
//
//        for (Pair<String, Integer> pair : users_commented) {
//            curator_rewards.add(new Pair<>(pair.first, max_comments / (double) pair.second));
//        }
//        for (Pair<String, Integer> pair : users_voted) {
//            curator_rewards.add(new Pair<>(pair.first, (double) max_comments));
//        }
//
//        double sum_curator_rewards = 0;
//        for (Pair<String, Double> pair : curator_rewards) {
//            sum_curator_rewards += pair.second;
//        }
//
//        for (Pair<String, Double> pair : curator_rewards) {
//            rewards.add(new Winsome_Reward(curator_reward * pair.second / sum_curator_rewards, pair.first));
//        }
//
//        return rewards;
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

    public int getN_rewards() { return n_rewards; }

    public Timestamp getTime_last_reward() { return time_last_reward; }

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

    public void setN_rewards(int n_rewards) { this.n_rewards = n_rewards; }

    public void setTime_last_reward(Timestamp time_last_reward) { this.time_last_reward = time_last_reward; }

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
