package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_comunication.Comment_representation;
import winsome_comunication.Post_representation_detailed;
import winsome_comunication.Post_representation_simple;
import winsome_server.*;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a post in the database.
 * It contains:
 * 1. The id of the post.
 * 2. The title of the post.
 * 3. The content of the post.
 * 4. The author of the post. (from User_interaction)
 * 5. The time the post was created. (from User_interaction)
 * 6. The comments of the post.
 * 7. The votes of the post.
 * 8. The number of times the post was rewarded.
 * 9. The last time the post was rewarded.
 * <p>
 * This class is available only to the Winsome_Database.
 */
public class Post_DB extends User_interaction implements JSON_Serializable {

	// Member variables
	private String id;
	private String title;
	private String text;
	private ArrayList<Comment_DB> comments;
	private ArrayList<VoteDB> votes;
	private int n_rewards;
	private Timestamp time_last_reward;

	// Constructors
	// Default Constructor
	public Post_DB(String id, String author, String title, String text) {
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
	public Post_DB() {

	}

	// JSON Methods
	public static Post_DB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), Post_DB.class);
	}
	@Override
	public void JSON_write(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(filePath), this);
	}

	// Getters
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getText() {
		return text;
	}
	public ArrayList<Comment_DB> getComments() {
		return comments;
	}
	public ArrayList<VoteDB> getVotes() {
		return votes;
	}
	public Timestamp getTime_last_reward() {
		return time_last_reward;
	}
	public int getN_rewards() {
		return n_rewards;
	}
	
	// Setters
	public void setId(String id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setComments(ArrayList<Comment_DB> comments) {
		this.comments = comments;
	}
	public void setVotes(ArrayList<VoteDB> votes) {
		this.votes = votes;
	}
	public void setTime_created(Timestamp time_created) {
		this.time_created = time_created;
	}
	public void setN_rewards(int n_rewards) {
		this.n_rewards = n_rewards;
	}
	public void setTime_last_reward(Timestamp time_last_reward) {
		this.time_last_reward = time_last_reward;
	}

	// Adders
	public void addComment(Comment_DB comment) {
		/*
		 * This method is used to add a comment to the list of comments of this post.
		 *
		 * 1. Add the comment to the list of comments of this post.
		 */

		// 1. Add the comment to the list of comments of this post.
		this.comments.add(comment);
	}
	public void addVote(VoteDB vote) {
		/*
		 * This method is used to add a vote to the list of votes of this post.
		 *
		 * 1. Add the vote to the list of votes of this post.
		 */

		// 1. Add the vote to the list of votes of this post.
		this.votes.add(vote);
	}
	
	// Representation
	public Post_representation_simple representation_simple() {
		/*
		 * This method is used to get a Post_simple object from this Post object.
		 *
		 * 1. Return the Post_simple object.
		 */

		// 1. Return the Post_simple object.
		return new Post_representation_simple(this.title, this.text, this.author, this.id);
	}
	public Post_representation_detailed representation_detailed() {
		/*
		 * This method is used to get a Post_detailed object from this Post object.
		 *
		 * 1. Return the Post_detailed object.
		 */

		// 1. Return the Post_detailed object.
		int upvotes = 0;
		int downvotes = 0;
		for (VoteDB vote : this.votes) {
			if (vote.getVote()) {
				upvotes++;
			} else {
				downvotes++;
			}
		}
		ArrayList<Comment_representation> comments = new ArrayList<>();
		for (Comment_DB comment : this.comments) {
			comments.add(comment.representation());
		}
		return new Post_representation_detailed(this.title, this.text, this.author, this.id, upvotes, downvotes, comments);
	}

	// Checkers
	public boolean has_vote_from(String user) {
		/*
		 * This method is used to check if a user has voted on this post.
		 *
		 * 1. Loop through the list of votes.
		 * 2. If the user has voted on this post, return true.
		 */

		// 1. Loop through the list of votes.
		for (VoteDB vote : this.votes) {
			// 2. If the user has voted on this post, return true.
			if (vote.getAuthor().equals(user)) {
				return true;
			}
		}
		return false;
	}

	// Other Methods
	public List<Winsome_Reward> calculate_rewards() {
		/*
		 * This method is used to calculate the reward of this post.
		 *
		 * 1. Get the list of votes and comments newer than the time_last_reward.
		 * 2. Calculate the reward of this post.
		 * 3. Return the reward of this post.
		 */

		class Pair<A, B> {
			public final A first;
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
		ArrayList<VoteDB> votes_to_elaborate = new ArrayList<>();
		ArrayList<VoteDB> votes_known = new ArrayList<>(votes);
		ArrayList<Comment_DB> comments_to_elaborate = new ArrayList<>();
		ArrayList<Comment_DB> comments_known = new ArrayList<>(comments);


		ArrayList<Pair<String, Integer>> users_commented = new ArrayList<>();
		ArrayList<Pair<String, Integer>> users_voted = new ArrayList<>();


		for (VoteDB vote : this.votes) {
			if (vote.getTime_created().after(this.time_last_reward)) {
				votes_to_elaborate.add(vote);
			}
		}
		for (Comment_DB comment : this.comments) {
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
		for (VoteDB vote : votes_to_elaborate) {
			if (vote.getVote()) {
				sum += 1;
				users_voted.add(new Pair<>(vote.getAuthor(), 1));
			} else {
				sum -= 1;
			}
		}
		res += Math.log(Math.max(sum, 0) + 1);

		// comments
		// logn(Sum(p = 0 to new_comments) (2/(1 + e^-(comments_made_by_user -1)) + 1
		sum = 0;
		for (Comment_DB comment : comments_to_elaborate) {
			users_commented.add(new Pair<>(comment.getAuthor(), 1));
			int comments_made_by_user = 0;
			for (Comment_DB comment2 : comments_known) {
				if (comment2.getAuthor().equals(comment.getAuthor())) {
					comments_made_by_user++;
					users_commented.get(users_commented.size() - 1).second++;
				}
			}
			sum += 2 / (1 + Math.exp(-(comments_made_by_user - 1))) + 1;
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
	
	@Override
	public String toString() {
		return "Post{" +
				"author='" + author + '\'' +
				", title='" + title + '\'' +
				", body='" + text + '\'' +
				", time_created=" + time_created +
				", time_last_reward=" + time_last_reward +
				", votes=" + votes +
				", comments=" + comments +
				", n_rewards=" + n_rewards +
				'}';
	}

	// Getters



}
