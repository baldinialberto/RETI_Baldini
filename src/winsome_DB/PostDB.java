package winsome_DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import winsome_communication.CommentRepr;
import winsome_communication.PostReprDetailed;
import winsome_communication.PostReprSimple;
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
public class PostDB extends User_interaction implements JSON_Serializable {

	// Member variables
	private String id;
	private String title;
	private String text;
	private ArrayList<Comment_DB> comments;
	private ArrayList<RateDB> rates;
	private int n_rewards;
	private Timestamp time_last_reward;

	public static final int TITLE_MAX_LENGTH = 20;
	public static final int CONTENT_MAX_LENGTH = 500;

	// Constructors
	// Default Constructor
	public PostDB(String id, String author, String title, String text) {
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
		this.rates = new ArrayList<>();

		// 7. Set the time_created Timestamp
		this.time_created = new Timestamp(System.currentTimeMillis());

		// 8. Set the number of rewards to 0.
		this.n_rewards = 0;

		// 9. Set the time_last_reward Timestamp.
		this.time_last_reward = new Timestamp(this.time_created.getTime());
	}

	// Jackson Constructor
	public PostDB() {

	}

	// JSON Methods
	public static PostDB JSON_read(String filePath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new File(filePath), PostDB.class);
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
	public ArrayList<RateDB> getRates() {
		return rates;
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
	public void setRates(ArrayList<RateDB> rates) {
		this.rates = rates;
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
	public void addVote(RateDB vote) {
		/*
		 * This method is used to add a vote to the list of votes of this post.
		 *
		 * 1. Add the vote to the list of votes of this post.
		 */

		// 1. Add the vote to the list of votes of this post.
		this.rates.add(vote);
	}
	
	// Representation
	public PostReprSimple representation_simple() {
		/*
		 * This method is used to get a Post_simple object from this Post object.
		 *
		 * 1. Return the Post_simple object.
		 */

		// 1. Return the Post_simple object.
		return new PostReprSimple(this.title, this.text, this.author, this.id);
	}
	public PostReprDetailed representation_detailed() {
		/*
		 * This method is used to get a Post_detailed object from this Post object.
		 *
		 * 1. Return the Post_detailed object.
		 */

		// 1. Return the Post_detailed object.
		int upvotes = 0;
		int downvotes = 0;
		for (RateDB vote : this.rates) {
			if (vote.getRate()) {
				upvotes++;
			} else {
				downvotes++;
			}
		}
		ArrayList<CommentRepr> comments = new ArrayList<>();
		for (Comment_DB comment : this.comments) {
			comments.add(comment.representation());
		}
		return new PostReprDetailed(this.title, this.text, this.author, this.id, upvotes, downvotes, comments);
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
		for (RateDB vote : this.rates) {
			// 2. If the user has voted on this post, return true.
			if (vote.getAuthor().equals(user)) {
				return true;
			}
		}
		return false;
	}

	// Other Methods
	public List<WinsomeReward> calculate_rewards(double author_reward) {
		/*
		 * This method is used to calculate the reward of this post.
		 *
		 * 1. Get the list of votes and comments newer than the time_last_reward.
		 * 2. Calculate the rewards of this post divided by the author and the curators.
		 *  (the author gets 80% of the rewards and the curators get 20% of the rewards)
		 * 3. Return the list of rewards.
		 */

		// 1. Get the list of votes and comments newer than the time_last_reward.
		List<RateDB> votes = new ArrayList<>();
		for (RateDB vote : this.rates) {
			if (vote.getTime_created().after(this.time_last_reward)) {
				votes.add(vote);
			}
		}
		List<Comment_DB> comments = new ArrayList<>();
		for (Comment_DB comment : this.comments) {
			if (comment.getTime_created().after(this.time_last_reward)) {
				comments.add(comment);
			}
		}

		// If there are no votes or comments, return null.
		if (votes.size() == 0 && comments.size() == 0) {
			return null;
		}

		// Otherwise, calculate the rewards.
		this.time_last_reward = new Timestamp(System.currentTimeMillis());
		this.n_rewards++;

		// 2. Calculate the rewards of this post divided by the author and the curators.
		//    (the author gets 80% of the rewards and the curators get 20% of the rewards)
		// Only positive votes and comments are rewarded.
		// The rewards are calculated as follows:
		//    - The author gets 80% of the total rewards.
		//    - The curators get 20% of the total rewards.
		//    - The rewards are divided equally among the curators (only those who liked or commented).
		// The total reward is calculated as follows:
		// For the votes:
		// logn(max(0, (sum of the new votes)) + 1) / n_rewards
		// For the comments:
		// logn(sum of the new comments, each comment is worth (2/(1 + e^(-Cp +1))) + 1)
		// [where Cp is the number of times the user has commented on this post]

		// Calculate the total reward.
		double total_reward = 0;
		List<WinsomeReward> rewards = new ArrayList<>();
		rewards.add(new WinsomeReward(0, this.author)); // The author is the first reward.

		// Calculate the reward for the votes.
		double sum_votes = 0;
		for (RateDB vote : votes) {
			if (vote.getRate()) {
				sum_votes++;
				rewards.add(new WinsomeReward(1, vote.getAuthor()));
			} else {
				sum_votes--;
			}
		}
		total_reward += Math.log(Math.max(0, sum_votes) + 1) / this.n_rewards;

		// Calculate the reward for the comments.
		double sum_comments = 0;
		int cp;
		for (Comment_DB comment : comments) {
			cp = get_ncomment_of_user(comment.getAuthor());
			sum_comments += 2 / (1 + Math.exp(-cp + 1));
			rewards.add(new WinsomeReward(cp, comment.getAuthor()));
		}
		total_reward += Math.log(sum_comments + 1) / this.n_rewards;

		// Calculate the rewards for the author and the curators.
		double reward_author = total_reward * author_reward;
		double reward_curators = total_reward - reward_author;

		// Assign the rewards to the author.
		rewards.get(0).value = reward_author;

		// Assign the rewards to the curators.
		// The current value of the rewards is used to calculate each curator's reward.
		// Each vote is worth 1, each comment is wort 1/n_comments (the current value of the reward).
		// The rewards are divided equally among the curators (so if a user has posted multiple comments, he will get
		// multiple smaller rewards).
		// For example there are 3 votes and 2 comments (1 is a first comment[1], another is a third comment[3]).
		// The user who posted the third comments gets 1/3 of the reward of a user who posted the first comment or rated.
		// The user who posted the first comment gets the same reward of a user who rated.

		// Calculate the total number of votes and comments.
		int n_curators = 0;
		for (WinsomeReward reward : rewards.subList(1, rewards.size())) {
			n_curators += reward.value;
		}

		// Calculate the reward for each curator.
		for (WinsomeReward reward : rewards.subList(1, rewards.size())) {
			reward.value = reward_curators * reward.value / n_curators;
		}

		// Calculate max and sum
		double max = 0, sum = 0;
		for (WinsomeReward reward : rewards.subList(1, rewards.size())) {
			if (reward.value > max) {
				max = reward.value;
			}
			sum += (1/reward.value);
		}

		// Assign the rewards to the curators.
		for (WinsomeReward reward : rewards.subList(1, rewards.size())) {
			reward.value = (reward_curators / sum) * (reward.value / max);
		}

		// Debug
		System.out.printf("Rewards for post %s : %s\n", this.id, rewards);
		
		return rewards;
	}

	private int get_ncomment_of_user(String username) {
		/*
		 * This method is used to get the number of comments of a user on this post.
		 *
		 * 1. Loop through the list of comments.
		 * 2. If the user has commented on this post, increase the counter.
		 * 3. Return the counter.
		 */

		// 1. Loop through the list of comments.
		int counter = 0;
		for (Comment_DB comment : this.comments) {
			// 2. If the user has commented on this post, increase the counter.
			if (comment.getAuthor().equals(username)) {
				counter++;
			}
		}
		// 3. Return the counter.
		return counter;
	}

	@Override
	public String toString() {
		return "Post{" +
				"author='" + author + '\'' +
				", title='" + title + '\'' +
				", body='" + text + '\'' +
				", time_created=" + time_created +
				", time_last_reward=" + time_last_reward +
				", votes=" + rates +
				", comments=" + comments +
				", n_rewards=" + n_rewards +
				'}';
	}

	// Getters



}
